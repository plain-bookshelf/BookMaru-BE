package plain.bookmaru.domain.lending.service

import io.github.oshai.kotlinlogging.KotlinLogging
import plain.bookmaru.common.annotation.Service
import plain.bookmaru.common.port.ConcurrencyPort
import plain.bookmaru.common.port.TransactionPort
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.inventory.port.out.BookAffiliationPort
import plain.bookmaru.domain.inventory.port.out.BookDetailPort
import plain.bookmaru.domain.lending.exception.NoMoreRentalException
import plain.bookmaru.domain.lending.exception.NotExistBookDetailException
import plain.bookmaru.domain.lending.exception.NotFirstReservationException
import plain.bookmaru.domain.lending.exception.OverdueException
import plain.bookmaru.domain.lending.model.Rental
import plain.bookmaru.domain.lending.model.Reservation
import plain.bookmaru.domain.lending.port.`in`.RentalUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.domain.lending.port.out.BookRentalRecordPort
import plain.bookmaru.domain.lending.port.out.BookReservationPort
import plain.bookmaru.domain.lending.vo.BookRecord
import plain.bookmaru.domain.manager.port.out.RentalRequestRealtimePort
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.port.out.MemberPort
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class RentalService(
    private val bookDetailPort: BookDetailPort,
    private val bookAffiliationPort: BookAffiliationPort,
    private val bookReservationPort: BookReservationPort,
    private val memberPort: MemberPort,
    private val bookRentalRecordPort: BookRentalRecordPort,
    private val transactionPort: TransactionPort,
    private val concurrencyPort: ConcurrencyPort,
    private val rentalRequestRealtimePort: RentalRequestRealtimePort
) : RentalUseCase {
    override suspend fun execute(command: LendingCommand) {
        concurrencyPort.executeWithRetry("rental-service") {
            val bookAffiliationId = command.bookAffiliationId
            val username = command.username

            val member = memberPort.findByUsername(username)
                ?: throw NotFoundMemberException("$username 사용자를 찾을 수 없습니다.")

            if (member.authority == Authority.ROLE_OVERDUE) {
                throw OverdueException("연체 상태에서는 대여할 수 없습니다.")
            }

            val count = member.lendingBook.rentalCount
            val availableRentalCount = when (member.authority) {
                Authority.ROLE_USER -> 3
                Authority.ROLE_MANAGER -> 10
                else -> 1000
            }

            if (count >= availableRentalCount) {
                throw NoMoreRentalException("$username 사용자는 더 이상 대여할 수 없습니다.")
            }

            val firstReservation = validateReservationPriority(bookAffiliationId, member.id!!)

            val bookDetail = bookDetailPort.findRentalBookDetailByBookAffiliationId(bookAffiliationId)
            log.info { "대여 가능한 책 상세 정보를 찾았습니다." }

            if (bookDetail == null) {
                throw NotExistBookDetailException("bookAffiliationId: $bookAffiliationId 에서 대여 가능한 책이 없습니다.")
            }

            val rental = Rental(
                memberId = member.id,
                bookDetailId = bookDetail.id!!,
                bookRecord = BookRecord(
                    rentalDate = LocalDateTime.now()
                )
            )

            member.incrementRentalCount()
            if (firstReservation != null) {
                member.decrementReservationCount()
            }

            val returnDate = if (member.authority == Authority.ROLE_TEACHER || member.authority == Authority.ROLE_LIBRARIAN) {
                LocalDate.now().plusDays(365)
            } else {
                LocalDate.now().plusDays(14)
            }

            transactionPort.withTransaction {
                bookDetailPort.updateRental(rental, returnDate)
                log.info { "책 대여자 정보와 책 상태 변경에 성공했습니다." }
                memberPort.save(member)
                log.info { "유저 대여 권 수 증가를 완료했습니다." }
                bookRentalRecordPort.save(rental)
                log.info { "대여 기록 저장에 성공했습니다." }

                if (firstReservation != null) {
                    bookReservationPort.deleteReservation(member.id, bookAffiliationId)
                    bookAffiliationPort.decrementReservationCount(bookAffiliationId)
                    log.info { "첫 번째 예약자의 예약을 소진했습니다." }
                }
            }

            val affiliationId = member.affiliationId ?: return@executeWithRetry
            val updatedRequests = bookRentalRecordPort.findRentalRequestBookByAffiliationId(affiliationId).orEmpty()
            rentalRequestRealtimePort.send(affiliationId, updatedRequests)
        }
    }

    private suspend fun validateReservationPriority(bookAffiliationId: Long, memberId: Long): Reservation? {
        val firstReservation = bookReservationPort.findFirstReservationByBookAffiliationId(bookAffiliationId)
            ?: return null

        if (firstReservation.member.id != memberId) {
            throw NotFirstReservationException(
                "bookAffiliationId: $bookAffiliationId 의 첫 번째 예약자만 대여 요청할 수 있습니다."
            )
        }

        return firstReservation
    }
}

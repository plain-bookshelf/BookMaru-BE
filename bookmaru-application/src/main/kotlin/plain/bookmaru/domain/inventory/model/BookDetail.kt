package plain.bookmaru.domain.inventory.model

import plain.bookmaru.common.annotation.Aggregate
import plain.bookmaru.domain.inventory.vo.BookDetailDiscernment
import plain.bookmaru.domain.inventory.vo.RentalStatus
import java.time.LocalDate

@Aggregate
class BookDetail(
    val id: Long? = null,
    val bookAffiliationId: Long,
    val bookDetailDiscernment: BookDetailDiscernment,
    rentalStatus: RentalStatus = RentalStatus.RETURN,
    memberId: Long? = null,
    returnDate: LocalDate? = null
) {
    var rentalStatus: RentalStatus = rentalStatus
        private set

    var memberId: Long? = memberId
        private set

    var returnDate: LocalDate? = returnDate
        private set

    fun requestRental(memberId: Long, returnDate: LocalDate): BookDetail {
        requireReturnStatus()

        this.rentalStatus = RentalStatus.RENTAL_REQUEST
        this.memberId = memberId
        this.returnDate = returnDate

        return this
    }

    fun approveRental(): BookDetail {
        require(rentalStatus == RentalStatus.RENTAL_REQUEST) {
            "대여 요청 상태인 책만 승인할 수 있습니다."
        }

        this.rentalStatus = RentalStatus.RENTAL

        return this
    }

    fun assignReturnedRental(memberId: Long, returnDate: LocalDate): BookDetail {
        requireReturnStatus()

        this.rentalStatus = RentalStatus.RENTAL
        this.memberId = memberId
        this.returnDate = returnDate

        return this
    }

    fun returnBook(): BookDetail {
        require(rentalStatus != RentalStatus.RETURN) {
            "대여 중인 책만 반납할 수 있습니다."
        }
        this.rentalStatus = RentalStatus.RETURN
        this.memberId = null
        this.returnDate = null

        return this
    }

    private fun requireReturnStatus() {
        require(rentalStatus == RentalStatus.RETURN) {
            "반납 상태인 책만 대여 처리할 수 있습니다."
        }
    }
}

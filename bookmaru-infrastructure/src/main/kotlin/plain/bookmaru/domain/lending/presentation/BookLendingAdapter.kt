package plain.bookmaru.domain.lending.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.lending.port.`in`.CancelReservationUseCase
import plain.bookmaru.domain.lending.port.`in`.RentalUseCase
import plain.bookmaru.domain.lending.port.`in`.ReservationUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/{bookAffiliationId}")
class BookLendingAdapter(
    private val rentalUseCase: RentalUseCase,
    private val reservationUseCase: ReservationUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase
) {

    @PostMapping("/rental")
    @LogExecution
    suspend fun rental(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = LendingCommand(
            bookAffiliationId = bookAffiliationId,
            username = principal.username.toString(),
        )

        rentalUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 대여에 성공했습니다.", ""))
    }

    @PostMapping("/reservation")
    @LogExecution
    suspend fun reservation(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = LendingCommand(
            bookAffiliationId = bookAffiliationId,
            username = principal.username.toString(),
        )

        reservationUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 예약에 성공했습니다.", ""))
    }

    @DeleteMapping("/reservation-delete")
    @LogExecution
    suspend fun cancelReservation(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = LendingCommand(
            bookAffiliationId = bookAffiliationId,
            username = principal.username.toString(),
        )

        cancelReservationUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "예약 취소에 성공했습니다.", ""))
    }
}

package plain.bookmaru.domain.lending.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.lending.port.`in`.RentalUseCase
import plain.bookmaru.domain.lending.port.`in`.command.LendingCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/api/{bookAffiliationId}")
class BookLendingAdapter(
    private val rentalUseCase: RentalUseCase
) {

    @PostMapping("/rental")
    @LogExecution
    suspend fun rental(
        @PathVariable bookAffiliationId: Long,
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = LendingCommand(
            bookAffiliationId = bookAffiliationId,
            username = principal.username,
        )

        rentalUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "책 대여에 성공했습니다.", ""))
    }
}
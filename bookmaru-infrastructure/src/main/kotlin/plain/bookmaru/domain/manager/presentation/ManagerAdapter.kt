package plain.bookmaru.domain.manager.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.manager.port.`in`.RentalRequestCheckUseCase
import plain.bookmaru.domain.manager.port.`in`.command.RentalRequestCheckCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/manager")
class ManagerAdapter(
    private val rentalRequestCheckUseCase: RentalRequestCheckUseCase
) {

    @GetMapping
    @LogExecution
    suspend fun getRentalRequestBook(
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = RentalRequestCheckCommand(principal.affiliationId)

        val result = rentalRequestCheckUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "대여 요청 정보를 가져오는데 성공했습니다.", result))
    }
}
package plain.bookmaru.domain.manager.presentation

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.command.PageCommand
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.manager.port.`in`.RentalBookStatusCheckSearchMemberUseCase
import plain.bookmaru.domain.manager.port.`in`.RentalBookStatusCheckUseCase
import plain.bookmaru.domain.manager.port.`in`.RentalRequestCheckUseCase
import plain.bookmaru.domain.manager.port.`in`.command.RentalBookStatusCheckCommand
import plain.bookmaru.domain.manager.port.`in`.command.RentalRequestCheckCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/manager")
class ManagerAdapter(
    private val rentalRequestCheckUseCase: RentalRequestCheckUseCase,
    private val rentalBookStatusCheckUseCase: RentalBookStatusCheckUseCase,
    private val rentalBookStatusCheckSearchMemberUseCase: RentalBookStatusCheckSearchMemberUseCase
) {

    @GetMapping("/rentalRequestCheck")
    @LogExecution
    suspend fun getRentalRequestCheck(
        @AuthenticationPrincipal principal: CustomUserDetails
    ) : ResponseEntity<SuccessResponse> {
        val command = RentalRequestCheckCommand(principal.affiliationId)

        val result = rentalRequestCheckUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "대여 요청 정보를 가져오는데 성공했습니다.", result))
    }

    @GetMapping("/rentalStatusCheck")
    @LogExecution
    suspend fun getRentalStatusCheck(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PageableDefault(size = 8) pageable: Pageable
    ) : ResponseEntity<SuccessResponse> {
        val command = RentalBookStatusCheckCommand(
            pageCommand = PageCommand(
                page = pageable.pageNumber,
                size = pageable.pageSize
            ),
            affiliationId = principal.affiliationId
        )

        val result = rentalBookStatusCheckUseCase.execute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "대여된 책들의 상태 정보를 가져오는데 성공했습니다.", result))
    }

    @GetMapping("/rentalStatusCheck/searchMember")
    @LogExecution
    suspend fun getRentalStatusCheckSearchMember(
        @AuthenticationPrincipal principal: CustomUserDetails,
        @PageableDefault(size = 8) pageable: Pageable,
        @RequestParam nickname: String
    ) : ResponseEntity<SuccessResponse> {
        val command = RentalBookStatusCheckCommand(
            pageCommand = PageCommand(
                page = pageable.pageNumber,
                size = pageable.pageSize
            ),
            affiliationId = principal.affiliationId,
            nickname = nickname
        )

        val result = rentalBookStatusCheckSearchMemberUseCase.searchMemberExecute(command)

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "대여된 책들의 상태 정보를 유저 이름으로 가져오는데 성공했습니다.", result))
    }
}
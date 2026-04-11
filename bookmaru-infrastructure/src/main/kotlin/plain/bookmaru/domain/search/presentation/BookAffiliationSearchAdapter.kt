package plain.bookmaru.domain.search.presentation

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
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.domain.search.port.`in`.AppBookAffiliationSearchUseCase
import plain.bookmaru.domain.search.port.`in`.WebBookAffiliationSearchUseCase
import plain.bookmaru.domain.search.port.`in`.command.BookAffiliationSearchCommand
import plain.bookmaru.global.security.userdetails.CustomUserDetails

@RestController
@RequestMapping("/search")
class BookAffiliationSearchAdapter(
    private val appBookAffiliationSearchUseCase: AppBookAffiliationSearchUseCase,
    private val webBookAffiliationSearchUseCase: WebBookAffiliationSearchUseCase
) {

    @GetMapping
    @LogExecution
    suspend fun search(
        @RequestParam platformType: String,
        @RequestParam keyword: String,
        @PageableDefault(size = 12) pageable: Pageable,
        @AuthenticationPrincipal principal: CustomUserDetails
    ): ResponseEntity<SuccessResponse> {
        val command = BookAffiliationSearchCommand(
            pageCommand = PageCommand(
                page = pageable.pageNumber,
                size = pageable.pageNumber
            ),
            affiliationId = principal.affiliationId,
            keyword = keyword
        )

        val realPlatformType = PlatformType.valueOf(platformType)

        val result = when (realPlatformType) {
            PlatformType.WEB -> webBookAffiliationSearchUseCase.webExecute(command)
            PlatformType.ANDROID, PlatformType.IOS -> appBookAffiliationSearchUseCase.appExecute(command)
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(SuccessResponse.success(CustomHttpStatus.OK, "검색하는데 성공했습니다.", result))
    }
}
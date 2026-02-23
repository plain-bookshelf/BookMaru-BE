package plain.bookmaru.domain.affiliation.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.annotation.LogExecution
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.affiliation.port.`in`.AffiliationViewUseCase
import plain.bookmaru.domain.affiliation.presentation.dto.response.AffiliationViewResponseDto

@RestController
@RequestMapping( "/affiliation")
class AffiliationAdapter(
    private val affiliationViewUseCase: AffiliationViewUseCase
) {

    @GetMapping("/view")
    @LogExecution
    suspend fun getView(): ResponseEntity<SuccessResponse> {

        val result = affiliationViewUseCase.execute()

        return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(SuccessResponse(CustomHttpStatus.OK, "", AffiliationViewResponseDto(result)))
    }
}
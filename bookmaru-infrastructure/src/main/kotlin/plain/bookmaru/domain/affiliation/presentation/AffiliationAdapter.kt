package plain.bookmaru.domain.affiliation.presentation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.error.CustomHttpStatus
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.affiliation.port.`in`.AffiliationViewUseCase

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping( "/affiliation")
class AffiliationAdapter(
    private val affiliationViewUseCase: AffiliationViewUseCase
) {

    @GetMapping("/view")
    suspend fun getView(): ResponseEntity<SuccessResponse> {

        log.info { "현재 존재하는 소속 조회 시도" }

        val result = affiliationViewUseCase.view()

        log.info { "소속 조회 성공 $now" }

        return ResponseEntity.status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(SuccessResponse(CustomHttpStatus.OK, "", result))
    }
}
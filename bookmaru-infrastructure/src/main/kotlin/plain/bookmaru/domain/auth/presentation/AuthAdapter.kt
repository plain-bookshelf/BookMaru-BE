package plain.bookmaru.domain.auth.presentation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import plain.bookmaru.common.success.SuccessResponse
import plain.bookmaru.domain.member.presentation.dto.request.SignupRequestDto

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/auth")
class AuthAdapter {


}
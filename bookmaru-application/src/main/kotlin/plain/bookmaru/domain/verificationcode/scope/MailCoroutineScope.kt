package plain.bookmaru.domain.verificationcode.scope

import kotlinx.coroutines.CoroutineScope

class MailCoroutineScope(
    val scope: CoroutineScope
) : CoroutineScope by scope {
}
package plain.bookmaru.domain.verification.scope

import kotlinx.coroutines.CoroutineScope

class MailCoroutineScope(
    val scope: CoroutineScope
) : CoroutineScope by scope
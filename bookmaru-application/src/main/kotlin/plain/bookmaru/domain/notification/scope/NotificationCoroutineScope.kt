package plain.bookmaru.domain.notification.scope

import kotlinx.coroutines.CoroutineScope

class NotificationCoroutineScope(
    val scope: CoroutineScope
) : CoroutineScope by scope

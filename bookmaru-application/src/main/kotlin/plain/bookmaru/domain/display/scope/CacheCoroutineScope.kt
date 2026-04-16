package plain.bookmaru.domain.display.scope

import kotlinx.coroutines.CoroutineScope

class CacheCoroutineScope(
    val scope: CoroutineScope
) : CoroutineScope by scope
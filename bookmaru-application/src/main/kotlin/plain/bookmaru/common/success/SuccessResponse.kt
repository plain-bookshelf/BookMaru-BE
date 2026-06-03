package plain.bookmaru.common.success

import plain.bookmaru.common.error.CustomHttpStatus

data class SuccessResponse(
    val status : CustomHttpStatus,
    val message : String,
    val data : Any? = null
) {
    companion object {
        fun success(status : CustomHttpStatus, message: String,  data : Any? = null) : SuccessResponse = SuccessResponse(status, message, data)
    }
}

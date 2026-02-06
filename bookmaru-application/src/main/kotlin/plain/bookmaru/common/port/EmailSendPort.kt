package plain.bookmaru.common.port

interface EmailSendPort {
    suspend fun send(email : String, code : String)
}
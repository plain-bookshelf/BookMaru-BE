package plain.bookmaru.domain.member.port.`in`.command

data class ChangePasswordCommand(
    val newPassword: String,
    val existingPassword: String,
    val username: String,
    val accessToken: String
) {
    init {
        require(newPassword.isNotEmpty())
        require(newPassword.length < 20)
        require(newPassword.first() in 'a'..'z' || newPassword.first() in 'A'..'Z') { "비밀번호 첫째 자리는 영문자이어야 합니다." }
        require(newPassword.last() in 'a'..'z' || newPassword.last() in 'A'..'Z') { "비밀번호 마지막 자리는 영문자이어야 합니다." }
    }
}
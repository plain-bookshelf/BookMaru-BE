package plain.bookmaru.domain.verification.model

import plain.bookmaru.domain.auth.vo.Authority
import kotlin.random.Random

class OfficialCode(
    val id: Long? = null,
    val affiliationId: Long,
    val role: Authority,
    val code: String
) {
    companion object{
        fun create(affiliationId: Long) : List<OfficialCode> {
            val list = mutableListOf<OfficialCode>()

            list.add(createManager(affiliationId))
            list.add(createLibrarian(affiliationId))
            list.add(createTeacher(affiliationId))

            return list
        }

        private fun createManager(affiliationId: Long) : OfficialCode = officialCode(affiliationId, Authority.ROLE_MANAGER)

        private fun createLibrarian(affiliationId: Long) : OfficialCode = officialCode(affiliationId, Authority.ROLE_LIBRARIAN)

        private fun createTeacher(affiliationId: Long) : OfficialCode = officialCode(affiliationId, Authority.ROLE_TEACHER)

        private fun officialCode(affiliationId: Long, role: Authority) : OfficialCode = OfficialCode(
            affiliationId = affiliationId,
            role = role,
            code = generateRandomCode()
        )

        private fun generateRandomCode(): String {
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9') + ('a'..'z')

            return (1..8)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
    }
}
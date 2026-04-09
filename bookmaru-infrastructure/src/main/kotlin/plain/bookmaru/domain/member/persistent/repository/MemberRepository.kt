package plain.bookmaru.domain.member.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

interface MemberRepository : JpaRepository<MemberEntity, Long> {
    fun findByUsername(username: String): MemberEntity?
    fun findByEmail(email: String): MemberEntity?
    override fun getReferenceById(id: Long): MemberEntity
}
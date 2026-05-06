package plain.bookmaru.domain.member.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import plain.bookmaru.domain.affiliation.persistent.entity.QAffiliationEntity
import plain.bookmaru.domain.affiliation.persistent.repository.AffiliationRepository
import plain.bookmaru.domain.display.port.out.result.UserRankInfoResult
import plain.bookmaru.domain.member.exception.NotFoundMemberException
import plain.bookmaru.domain.member.model.Member
import plain.bookmaru.domain.member.persistent.entity.QMemberEntity
import plain.bookmaru.domain.member.persistent.mapper.MemberMapper
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.domain.member.port.out.MemberPort
import plain.bookmaru.global.config.DbProtection
import java.time.LocalDateTime

@Component
class MemberPersistenceAdapter(
    private val affiliationRepository : AffiliationRepository,
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val dbProtection: DbProtection,
    private val queryFactory: JPAQueryFactory
) : MemberPort {
    private val member = QMemberEntity.memberEntity
    private val affiliation = QAffiliationEntity.affiliationEntity

    override suspend fun findById(memberId: Long): Member? = dbProtection.withReadOnly {
        memberRepository.findByIdOrNull(memberId)
            ?.let(memberMapper::toDomain)
    }

    override fun save(member: Member): Member {
        val affiliationProxy = affiliationRepository.getReferenceById(member.affiliationId!!)

        if (member.id == null) {
            val memberEntity = memberMapper.toEntity(member, affiliationProxy)
            val saved = memberRepository.save(memberEntity)
            return memberMapper.toDomain(saved)
        } else {
            val existingEntity = memberRepository.findById(member.id!!)
                .orElseThrow { throw NotFoundMemberException("존재하지 않는 유저입니다.") }

            memberMapper.updateEntity(member, existingEntity, affiliationProxy)
            val saved = memberRepository.save(existingEntity)

            return memberMapper.toDomain(saved)
        }
    }

    override fun applyOverduePenalty(memberId: Long, overdueDays: Long) {
        val memberEntity = memberRepository.findById(memberId)
            .orElseThrow { throw NotFoundMemberException("존재하지 않는 사용자입니다.") }
        val currentDateTime = LocalDateTime.now()

        memberEntity.overdueStatus = true
        val existingOverdueTerm = memberEntity.overdueTerm
        memberEntity.overdueTerm = if (existingOverdueTerm != null && existingOverdueTerm.isAfter(currentDateTime)) {
            existingOverdueTerm.plusDays(overdueDays)
        } else {
            currentDateTime.plusDays(overdueDays)
        }
    }

    override suspend fun markOverdueMembers(memberIds: Collection<Long>): Long = dbProtection.withTransaction {
        if (memberIds.isEmpty()) return@withTransaction 0

        return@withTransaction queryFactory
            .update(member)
            .set(member.overdueStatus, true)
            .where(
                member.id.`in`(memberIds),
                member.deleteStatus.eq(false)
            )
            .execute()
    }

    override suspend fun releaseExpiredOverduePenalties(
        now: LocalDateTime,
        activeOverdueMemberIds: Collection<Long>
    ): Long = dbProtection.withTransaction {
        val conditions = mutableListOf(
            member.overdueStatus.eq(true),
            member.overdueTerm.isNotNull,
            member.overdueTerm.loe(now),
            member.deleteStatus.eq(false)
        )

        if (activeOverdueMemberIds.isNotEmpty()) {
            conditions += member.id.notIn(activeOverdueMemberIds)
        }

        return@withTransaction queryFactory
            .update(member)
            .set(member.overdueStatus, false)
            .set(member.overdueTerm, null as LocalDateTime?)
            .where(*conditions.toTypedArray())
            .execute()

    }

    override suspend fun resetAllOneMonthStatistics(): Long = dbProtection.withTransaction {
        return@withTransaction queryFactory
            .update(member)
            .set(member.oneMonthStatistics, 0)
            .execute()
    }

    override suspend fun findByEmail(email: String): Member? = dbProtection.withReadOnly {
        memberRepository.findByEmail(email)?.let {
            memberMapper.toDomain(it)
        }
    }

    override suspend fun findAllByAffiliationId(affiliationId: Long): List<Member> = dbProtection.withReadOnly {
        val members = memberRepository.findAllByAffiliationEntityIdAndDeleteStatusFalse(affiliationId)

        return@withReadOnly memberMapper.toDomainList(members)
    }

    override suspend fun findUserRanking(affiliationId: Long): List<UserRankInfoResult> = dbProtection.withReadOnly {
        val entities = queryFactory
            .select(
                member.id!!,
                member.nickname,
                member.oneMonthStatistics,
                member.profileImage,
                affiliation.affiliationName
            )
            .from(member)
            .join(member.affiliationEntity, affiliation)
            .where(
                member.affiliationEntity.id.eq(affiliationId),
                member.deleteStatus.eq(false)
            )
            .orderBy(
                member.oneMonthStatistics.desc(),
                member.id.asc()
            )
            .limit(100)
            .fetch()

        return@withReadOnly entities.mapIndexed { index, entity ->
            UserRankInfoResult(
                memberId = entity.get(member.id)!!,
                rank = index + 1,
                nickName = entity.get(member.nickname)!!,
                oneMonthStatistics = entity.get(member.oneMonthStatistics)!!,
                affiliationName = entity.get(affiliation.affiliationName)!!,
                profileImage = entity.get(member.profileImage) ?: ""
            )
        }
    }

    override suspend fun validateNickname(nickname: String): Member? = dbProtection.withReadOnly {
        return@withReadOnly memberRepository.findByNickname(nickname)
            ?.let { memberMapper.toDomain(it) }
    }

    override suspend fun delete(member: Member): Unit = dbProtection.withTransaction {
        val affiliationProxy = affiliationRepository.getReferenceById(member.affiliationId!!)

        val memberEntity = memberRepository.findById(member.id!!)
            .orElseThrow { throw NotFoundMemberException("존재하지 않는 유저입니다.") }

        memberMapper.updateEntity(member, memberEntity, affiliationProxy)
        memberRepository.save(memberEntity)
    }

    override suspend fun findByUsername(username: String): Member? = dbProtection.withReadOnly {
        memberRepository.findByUsername(username)?.let {
            memberMapper.toDomain(it)
        }
    }
}

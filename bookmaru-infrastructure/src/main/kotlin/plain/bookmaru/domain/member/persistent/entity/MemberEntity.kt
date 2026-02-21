package plain.bookmaru.domain.member.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.member.persistent.converter.EmailConverter
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@SequenceGenerator(
    name = "member_seq_generator",
    sequenceName = "members_seq",
    allocationSize = 50
)
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @Column(nullable = false, unique = true)
    val id: Long? = null,

    @ManyToOne(optional = false, cascade = [CascadeType.REFRESH], fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false )
    val affiliation: AffiliationEntity,

    @Column(nullable = false, length = 20)
    val username: String,

    @Column(nullable = false, length = 20)
    val nickname: String,

    @Column(nullable = false, length = 100)
    val password: String,

    @Column(unique = true,length = 45)
    @Convert(converter = EmailConverter::class)
    val email: Email?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Authority,

    @Column(nullable = false, length = 100)
    val profileImage: String,

    @Column(nullable = false, precision = 1000)
    val oneMonthStatics: Int? = 0,

    val overdueTerm: LocalDateTime? = null,

    val bookReadTime: LocalDateTime? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberEntity) return false

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
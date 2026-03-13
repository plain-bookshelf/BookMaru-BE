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
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.domain.member.persistent.converter.EmailConverter
import plain.bookmaru.domain.member.vo.Email
import plain.bookmaru.domain.notification.persistent.entity.NotificationEntity
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@SequenceGenerator(
    name = "member_seq_generator",
    sequenceName = "members_seq",
    allocationSize = 50
)
class MemberEntity(
    @ManyToOne(optional = false, cascade = [CascadeType.REFRESH], fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false )
    var affiliation: AffiliationEntity,

    @Column(nullable = true, length = 45)
    val username: String,

    @Column(nullable = false, length = 45)
    var nickname: String,

    @Column(unique = true,length = 45)
    @Convert(converter = EmailConverter::class)
    val email: Email,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Authority,
) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val notificationEntities: MutableList<NotificationEntity> = mutableListOf()

    @Column(nullable = true, length = 100)
    var password: String? = null

    @Column(nullable = true, length = 100)
    var profileImage: String? = null

    @Column(nullable = false, precision = 1000)
    var oneMonthStatics: Int? = 0

    var overdueTerm: LocalDateTime? = null

    var oftenBookReadTime: LocalTime? = null
}
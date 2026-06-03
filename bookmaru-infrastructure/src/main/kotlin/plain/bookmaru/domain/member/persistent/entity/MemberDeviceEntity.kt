package plain.bookmaru.domain.member.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import plain.bookmaru.domain.auth.vo.PlatformType
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "member_device_seq_generator",
    sequenceName = "member_device_seq",
    allocationSize = 10
)
@Table(
    name = "member_device",
    indexes = [
        Index(name = "idx_member_device_member_id", columnList = "member_id"),
        Index(name = "idx_member_device_token", columnList = "device_token", unique = true)
    ]
)
class MemberDeviceEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var memberEntity: MemberEntity,

    @Column(name = "device_token", nullable = false, length = 255, unique = true)
    var deviceToken: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "platform_type", length = 10, nullable = false)
    var platformType: PlatformType
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_device_seq_generator")
    @Column(unique = true, nullable = false)
    override val id: Long? = null
}

package plain.bookmaru.domain.notification.persistent.entity

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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.TargetType
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "notification_seq_generator",
    sequenceName = "notification_seq",
    allocationSize = 50
)
@Table(
    name = "notification",
    indexes = [Index(name = "idx_target_id", columnList = "target_id")]
)
class NotificationEntity(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    val memberEntity: MemberEntity,

    @Column(nullable = false, name = "target_id")
    val targetId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val targetType: TargetType,

    @Column(nullable = false)
    val name: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    val payload: NotificationPayload,

    val type: NotificationType,

    val url: String
): BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null

    var isRead: Boolean = false
}
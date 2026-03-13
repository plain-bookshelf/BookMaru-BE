package plain.bookmaru.domain.notification.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.domain.notification.vo.NotificationPayload
import plain.bookmaru.domain.notification.vo.TargetType
import plain.bookmaru.domain.notification.vo.NotificationType
import plain.bookmaru.global.entity.BaseEntity

@Entity
class NotificationEntity(
    @Id @GeneratedValue
    override val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    val memberEntity: MemberEntity,

    @Column(nullable = false)
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

    var isRead: Boolean = false,

    val url: String
): BaseEntity()
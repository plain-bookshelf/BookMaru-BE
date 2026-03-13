package plain.bookmaru.domain.event.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.event.vo.EventType
import plain.bookmaru.domain.member.persistent.entity.MemberEntity
import plain.bookmaru.global.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@SequenceGenerator(
    name = "event_seq_generator",
    sequenceName = "event_seq",
    allocationSize = 5
)
class EventEntity(
    @Id @GeneratedValue
    override val id: Long? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val memberEntity: MemberEntity,

    var title: String,

    @Enumerated(EnumType.STRING)
    var status: EventType,

    var imageUrl: String,

    var startAt: LocalDateTime,

    var endAt: LocalDateTime
) : BaseEntity()
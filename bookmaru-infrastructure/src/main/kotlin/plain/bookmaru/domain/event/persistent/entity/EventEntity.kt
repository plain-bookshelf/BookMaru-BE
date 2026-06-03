package plain.bookmaru.domain.event.persistent.entity

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
@Table(
    name = "event",
    indexes = [Index(name = "idx_event_member_id", columnList = "member_id")]
)
class EventEntity(
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val memberEntity: MemberEntity,

    var title: String,

    @Enumerated(EnumType.STRING)
    var status: EventType,

    var imageUrl: String,

    var startAt: LocalDateTime,

    var endAt: LocalDateTime
) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null
}
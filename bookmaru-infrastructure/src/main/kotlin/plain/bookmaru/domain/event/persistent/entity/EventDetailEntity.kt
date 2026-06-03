package plain.bookmaru.domain.event.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import plain.bookmaru.global.entity.BaseEntity

@Entity
@Table(name = "event_detail")
class EventDetailEntity(
    @Id
    override val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    val event: EventEntity,

    var content: String
) : BaseEntity()
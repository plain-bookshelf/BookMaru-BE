package plain.bookmaru.domain.verification.persistent.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
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
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "affiliation_seq_generator",
    sequenceName = "affiliation_seq",
    allocationSize = 50
)
class OfficialCodeEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "affiliation_seq_generator")
    override val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], optional = false)
    @JoinColumn(name = "affiliation_id", nullable = false)
    val affiliation: AffiliationEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Authority,

    @Column(nullable = false, length = 10)
    val code: String
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OfficialCodeEntity) return false

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
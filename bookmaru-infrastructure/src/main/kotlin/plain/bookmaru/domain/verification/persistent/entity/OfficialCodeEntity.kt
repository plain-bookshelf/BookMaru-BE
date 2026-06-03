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
import jakarta.persistence.Table
import plain.bookmaru.domain.affiliation.persistent.entity.AffiliationEntity
import plain.bookmaru.domain.auth.vo.Authority
import plain.bookmaru.global.entity.BaseEntity

@Entity
@SequenceGenerator(
    name = "affiliation_seq_generator",
    sequenceName = "affiliation_seq",
    allocationSize = 50
)
@Table(name = "official_code")
class OfficialCodeEntity(
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], optional = false)
    @JoinColumn(name = "affiliation_id", nullable = false)
    val affiliation: AffiliationEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Authority,

    @Column(nullable = false, length = 10)
    val code: String
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "affiliation_seq_generator")
    @Column(nullable = false, unique = true)
    override val id: Long? = null
}
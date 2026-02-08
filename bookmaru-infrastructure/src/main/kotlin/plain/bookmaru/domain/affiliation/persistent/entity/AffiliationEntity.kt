package plain.bookmaru.domain.affiliation.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import plain.bookmaru.domain.member.persistent.entity.MemberEntity

@Entity
@SequenceGenerator(
    name = "affiliation_seq_generator",
    sequenceName = "affiliation_seq",
    allocationSize = 50
)
class AffiliationEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "affiliation_seq_generator")
    @Column(nullable = false, unique = true)
    val id : Long? = null,

    @OneToMany(mappedBy = "affiliation", fetch = FetchType.LAZY)
    private val members : MutableList<MemberEntity> = mutableListOf(),

    @Column(nullable = false, length = 45)
    val affiliationName : String
) {
}
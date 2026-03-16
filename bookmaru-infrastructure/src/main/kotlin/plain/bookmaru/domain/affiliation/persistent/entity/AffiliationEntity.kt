package plain.bookmaru.domain.affiliation.persistent.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@SequenceGenerator(
    name = "affiliation_seq_generator",
    sequenceName = "affiliation_seq",
    allocationSize = 50
)
@Table(name = "affiliation")
class AffiliationEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "affiliation_seq_generator")
    @Column(nullable = false, unique = true)
    val id : Long? = null,

    @Column(nullable = false, length = 45)
    val affiliationName : String
) {
}
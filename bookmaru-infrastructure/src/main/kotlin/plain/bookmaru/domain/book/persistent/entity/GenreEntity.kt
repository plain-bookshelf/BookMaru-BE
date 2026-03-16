package plain.bookmaru.domain.book.persistent.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@SequenceGenerator(
    name = "genre_seq_generator",
    sequenceName = "genre_seq",
    allocationSize = 50
)
@Table(name = "genre")
class GenreEntity(
    @Id @GeneratedValue
    val id : Long? = null,

    val genreName: String
)
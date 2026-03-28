package plain.bookmaru.domain.community.persistent.repository

import org.springframework.data.jpa.repository.JpaRepository
import plain.bookmaru.domain.community.persistent.entity.BookLikeEntity
import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId

interface BookLikeRepository : JpaRepository<BookLikeEntity, BookLikeEmbeddedId> {
    fun findBookLikeEntityById(id: BookLikeEmbeddedId) : BookLikeEntity?
}
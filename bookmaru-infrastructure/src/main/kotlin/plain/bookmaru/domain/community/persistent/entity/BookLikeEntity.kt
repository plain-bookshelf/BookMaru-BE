    package plain.bookmaru.domain.community.persistent.entity

    import jakarta.persistence.EmbeddedId
    import jakarta.persistence.Entity
    import jakarta.persistence.FetchType
    import jakarta.persistence.JoinColumn
    import jakarta.persistence.ManyToOne
    import jakarta.persistence.MapsId
    import jakarta.persistence.Table
    import plain.bookmaru.domain.community.persistent.entity.embedded.BookLikeEmbeddedId
    import plain.bookmaru.domain.inventory.persistent.entity.BookAffiliationEntity
    import plain.bookmaru.domain.member.persistent.entity.MemberEntity
    import plain.bookmaru.global.entity.BaseEntity

    @Entity
    @Table(name = "book_like")
    class BookLikeEntity(
        @EmbeddedId
        override val id: BookLikeEmbeddedId,

        @MapsId("memberId")
        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id")
        val memberEntity: MemberEntity,

        @MapsId("bookAffiliationId")
        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(name = "book_affiliation_id")
        val bookAffiliationEntity: BookAffiliationEntity
    ) : BaseEntity()
package plain.bookmaru.domain.search.persistent.document

import jakarta.persistence.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import org.springframework.data.elasticsearch.annotations.Setting
import plain.bookmaru.domain.inventory.model.BookAffiliation

@Document(indexName = "books_index")
@Setting(settingPath = "elasticsearch/settings.json")
data class BookAffiliationDocument(
    @Id
    val id: Long? = null,

    @Field(type = FieldType.Long)
    val affiliationId: Long,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori_analyzer"),
        otherFields = [
            InnerField(suffix = "ac", type = FieldType.Text, analyzer = "nori_autocomplete_analyzer")
        ]
    )
    val title: String,

    @Field(type = FieldType.Keyword)
    val author: String,

    @Field(type = FieldType.Text)
    val publicationDate: String,

    @Field(type = FieldType.Keyword)
    val publisher: String,

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    val introduction: String,

    @Field(type = FieldType.Keyword)
    val genres: List<String>
) {
    companion object {
        fun toDocument(bookAffiliation: BookAffiliation): BookAffiliationDocument {
            return BookAffiliationDocument(
                id = bookAffiliation.id,
                affiliationId = bookAffiliation.affiliationId,
                title = bookAffiliation.book.bookInfo.title,
                author = bookAffiliation.book.bookInfo.author,
                publicationDate = bookAffiliation.book.bookInfo.publicationDate,
                publisher = bookAffiliation.book.bookInfo.publisher,
                introduction = bookAffiliation.book.bookInfo.introduction,
                genres = bookAffiliation.book.genres?.map { it.genre.genreName } ?: emptyList()
            )
        }
    }
}
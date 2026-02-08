package plain.bookmaru.domain.member.persistent.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import plain.bookmaru.domain.member.vo.Email

@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {

    override fun convertToDatabaseColumn(attribute: Email?): String? {
        return attribute?.email
    }

    override fun convertToEntityAttribute(dbData: String?): Email? {
        return dbData?.let { Email(it) }
    }
}
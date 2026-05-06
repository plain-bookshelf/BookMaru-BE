package plain.bookmaru.domain.notification.vo

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import plain.bookmaru.domain.notification.vo.NotificationPayload.EventPayload
import plain.bookmaru.domain.notification.vo.NotificationPayload.OverduePayload
import plain.bookmaru.domain.notification.vo.NotificationPayload.RentalPayload
import plain.bookmaru.domain.notification.vo.NotificationPayload.ReservationPayload

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = RentalPayload::class, name = "RENTAL"),
    JsonSubTypes.Type(value = ReservationPayload::class, name = "RESERVATION"),
    JsonSubTypes.Type(value = EventPayload::class, name = "EVENT"),
    JsonSubTypes.Type(value = OverduePayload::class, name = "OVERDUE")
)
sealed interface NotificationPayload {
    data class RentalPayload(
        val bookId: Long,
        val title: String,
        val returnDate: String,
        val bookImage: String = ""
    ) : NotificationPayload

    data class ReservationPayload(
        val bookId: Long,
        val title: String,
        val returnDate: String,
        val bookImage: String = ""
    ) : NotificationPayload

    data class EventPayload(
        val eventId: Long,
        val title: String,
        val startDate: String,
        val endDate: String
    ) : NotificationPayload

    data class OverduePayload(
        val bookId: Long,
        val title: String,
        val returnDate: String,
        val bookImage: String = ""
    ) : NotificationPayload
}

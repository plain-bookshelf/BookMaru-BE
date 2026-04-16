package plain.bookmaru.domain.event.persistent

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.event.persistent.entity.QEventDetailEntity
import plain.bookmaru.domain.event.persistent.entity.QEventEntity
import plain.bookmaru.domain.event.port.out.EventDetailPort
import plain.bookmaru.domain.event.port.out.result.ViewEventDetailPageResult
import plain.bookmaru.global.config.DbProtection

@Component
class EventDetailPersistenceAdapter(
    private val queryFactory: JPAQueryFactory,
    private val dbProtection: DbProtection
): EventDetailPort {
    private val event = QEventEntity.eventEntity
    private val eventDetail = QEventDetailEntity.eventDetailEntity

    override suspend fun findById(eventId: Long): ViewEventDetailPageResult? = dbProtection.withReadOnly {
        return@withReadOnly queryFactory
            .select(
                Projections.constructor(
                    ViewEventDetailPageResult::class.java,
                    event.title,
                    event.status,
                    event.imageUrl,
                    event.startAt,
                    event.endAt,
                    eventDetail.content
                )
            )
            .from(eventDetail)
            .join(eventDetail.event, event)
            .where(eventDetail.event.id.eq(eventId))
            .fetchOne()
    }
}
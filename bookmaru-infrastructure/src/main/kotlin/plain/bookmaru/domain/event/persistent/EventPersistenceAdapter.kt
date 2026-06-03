package plain.bookmaru.domain.event.persistent

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import plain.bookmaru.domain.event.exception.NotContainEventContentException
import plain.bookmaru.domain.event.exception.NotFoundEventException
import plain.bookmaru.domain.event.model.Event
import plain.bookmaru.domain.event.persistent.entity.QEventDetailEntity
import plain.bookmaru.domain.event.persistent.entity.QEventEntity
import plain.bookmaru.domain.event.persistent.mapper.EventMapper
import plain.bookmaru.domain.event.persistent.repository.EventDetailRepository
import plain.bookmaru.domain.event.persistent.repository.EventRepository
import plain.bookmaru.domain.event.port.out.EventPort
import plain.bookmaru.domain.member.persistent.repository.MemberRepository
import plain.bookmaru.global.config.DbProtection

@Component
class EventPersistenceAdapter(
    private val eventRepository: EventRepository,
    private val eventDetailRepository: EventDetailRepository,
    private val memberRepository: MemberRepository,
    private val eventMapper: EventMapper,
    private val dbProtection: DbProtection,
    private val queryFactory: JPAQueryFactory
) : EventPort{
    private val event = QEventEntity.eventEntity
    private val eventDetail = QEventDetailEntity.eventDetailEntity

    override suspend fun findAll(): List<Event>? = dbProtection.withReadOnly {
        eventMapper.toDomainList(eventRepository.findAll())
    }

    override suspend fun findById(eventId: Long): Event? = dbProtection.withReadOnly {

        val result = queryFactory
            .select(event, eventDetail)
            .from(eventDetail)
            .join(eventDetail.event, event).fetchJoin()
            .where(eventDetail.event.id.eq(eventId))
            .fetchOne()

        val eventEntity = result?.get(event) ?: throw NotFoundEventException("eventId: $eventId 이벤트 아이디 정보를 찾지 못 했습니다.")
        val eventDetailEntity = result.get(eventDetail)

        val event = eventMapper.toDomain(eventEntity, eventDetailEntity)
        return@withReadOnly event
    }

    override suspend fun deleteById(eventId: Long): Unit = dbProtection.withTransaction {
        eventDetailRepository.deleteById(eventId)
        eventRepository.deleteById(eventId)
    }

    override suspend fun save(event: Event): Event = dbProtection.withTransaction {
        val member = memberRepository.getReferenceById(event.memberId)
        val eventEntity = eventMapper.toEntity(event, member)
        val eventRoot = eventEntity.first

        val eventDetail = eventEntity.second
            ?: throw NotContainEventContentException()

        val savedEvent = eventRepository.save(eventRoot)
        val savedDetail = eventDetailRepository.save(eventDetail)

        return@withTransaction eventMapper.toDomain(savedEvent, savedDetail)
    }
}

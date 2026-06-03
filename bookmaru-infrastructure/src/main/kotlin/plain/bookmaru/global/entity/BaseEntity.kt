package plain.bookmaru.global.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    abstract val id: Any?

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        if (getRealClass(this) != getRealClass(other)) return false

        other as BaseEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    private fun getRealClass(obj: Any): Class<*> {
        return if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.persistentClass
        } else {
            obj.javaClass
        }
    }
}
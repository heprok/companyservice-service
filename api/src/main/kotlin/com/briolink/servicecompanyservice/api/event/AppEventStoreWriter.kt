package com.briolink.servicecompanyservice.api.event

import com.briolink.event.publisher.EventStoreWriter
import com.briolink.servicecompanyservice.common.jpa.write.entity.EventStoreWriteEntity
import com.briolink.servicecompanyservice.common.jpa.write.repository.EventStoreWriteRepository
import org.springframework.stereotype.Component
aimport org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class AppEventStoreWriter(
    private val eventStoreWriteRepository: EventStoreWriteRepository,
) : EventStoreWriter{
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun write(payload: String, timestamp: Long) {
        // TODO: check for select
        eventStoreWriteRepository.save(EventStoreWriteEntity(payload, Instant.ofEpochMilli(timestamp)))
    }
}

package com.briolink.servicecompanyservice.api.event

import com.briolink.servicecompanyservice.common.jpa.write.entity.EventStoreWriteEntity
import com.briolink.servicecompanyservice.common.jpa.write.repository.EventStoreWriteRepository
import com.briolink.event.AbstractEventPublisherHandler
import org.springframework.stereotype.Component
import com.amazonaws.services.sns.AmazonSNS
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate
import java.time.Instant

@Component
class EventPublisherHandler(
    override val notificationMessagingTemplate: NotificationMessagingTemplate,
    private val eventStoreWriteRepository: EventStoreWriteRepository,
) :
    AbstractEventPublisherHandler() {
    override fun writeToEventStore(payload: String, timestamp: Long) {
        // TODO: check for select
        eventStoreWriteRepository.saveAndFlush(EventStoreWriteEntity(payload, Instant.ofEpochMilli(timestamp)))
    }
}

package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceDeletedEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("CompanyServiceUpdatedEvent", "1.0"),
    EventHandler("CompanyServiceCreatedEvent", "1.0"),
)
class ServiceUpdatedEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        companyServiceHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("CompanyServiceDeletedEvent", "1.0")
@Transactional
class ServiceDeletedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
) : IEventHandler<CompanyServiceDeletedEvent> {
    override fun handle(event: CompanyServiceDeletedEvent) {
        serviceReadRepository.deleteById(event.data.id)
    }
}

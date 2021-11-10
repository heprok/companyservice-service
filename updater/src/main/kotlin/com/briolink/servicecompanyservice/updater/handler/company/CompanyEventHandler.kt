package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
        EventHandler("CompanyCreatedEvent", "1.0"),
        EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyCreatedEventHandler(
    private val companyHandlerService: CompanyHandlerService
) : IEventHandler<CompanyEvent> {
    override fun handle(event: CompanyEvent) {
        companyHandlerService.createOrUpdate(event.data)
    }
}



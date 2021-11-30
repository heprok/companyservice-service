package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.updater.handler.companyservice.CompanyServiceHandlerService
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionHandlerService

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val connectionHandlerService: ConnectionHandlerService
) : IEventHandler<CompanyEvent> {
    override fun handle(event: CompanyEvent) {
        companyHandlerService.createOrUpdate(event.data).let {
            if (event.name == "CompanyUpdatedEvent") {
                companyServiceHandlerService.updateCompany(it)
                connectionHandlerService.updateCompany(it)
            }
        }
    }
}

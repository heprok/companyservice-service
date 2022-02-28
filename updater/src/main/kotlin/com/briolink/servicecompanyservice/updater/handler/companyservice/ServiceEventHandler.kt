package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceDeletedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceSyncEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.updater.service.SyncService
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("CompanyServiceUpdatedEvent", "1.0"),
    EventHandler("CompanyServiceCreatedEvent", "1.0"),
)
class CompanyServiceUpdatedEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        companyServiceHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("CompanyServiceDeletedEvent", "1.0")
@Transactional
class CompanyServiceDeletedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
) : IEventHandler<CompanyServiceDeletedEvent> {
    override fun handle(event: CompanyServiceDeletedEvent) {
        serviceReadRepository.deleteById(event.data.id)
    }
}

@EventHandler("CompanyServiceSyncEvent", "1.0")
class CompanyServiceSyncEventHandler(
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    syncService: SyncService,
) : SyncEventHandler<CompanyServiceSyncEvent>(ObjectSyncEnum.CompanyService, syncService) {
    override fun handle(event: CompanyServiceSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            if (objectSync.deleted) companyServiceHandlerService.deleteById(objectSync.id)
            else companyServiceHandlerService.createOrUpdate(objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceDeletedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceSyncEvent
import com.briolink.servicecompanyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.updater.service.SyncService
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

@EventHandler("CompanyServiceSyncEvent", "1.0")
class CompanyServiceSyncEventHandler(
    private val syncService: SyncService,
    private val companyServiceHandlerService: CompanyServiceHandlerService,
) : IEventHandler<CompanyServiceSyncEvent> {
    override fun handle(event: CompanyServiceSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            companyServiceHandlerService.createOrUpdate(event.data.objectSync)
        } catch (ex: Exception) {
            syncService.sendSyncError(
                syncError = SyncError(
                    service = syncData.service,
                    updater = UpdaterEnum.CompanyService,
                    syncId = syncData.syncId,
                    exception = ex,
                    indexObjectSync = syncData.indexObjectSync,
                ),
            )
        }
        println("Syncindex : " + syncData.indexObjectSync)
        println("Total : " + syncData.totalObjectSync)
        if (syncData.indexObjectSync == syncData.totalObjectSync)
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyService)
    }
}

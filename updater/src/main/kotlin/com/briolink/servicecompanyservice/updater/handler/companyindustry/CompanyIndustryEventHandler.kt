package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.updater.service.SyncService

@EventHandler("IndustryCreatedEvent", "1.0")
class CompanyIndustryCreatedEventHandler(
    private val companyIndustryHandlerService: CompanyIndustryHandlerService,
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        companyIndustryHandlerService.createOrUpdate(event.data)
    }
}

@EventHandler("IndustrySyncEvent", "1.0")
class IndustrySyncEventHandler(
    private val companyIndustryHandlerService: CompanyIndustryHandlerService,
    syncService: SyncService,
) : SyncEventHandler<IndustrySyncEvent>(ObjectSyncEnum.CompanyIndustry, syncService) {
    override fun handle(event: IndustrySyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            companyIndustryHandlerService.createOrUpdate(objectSync)
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

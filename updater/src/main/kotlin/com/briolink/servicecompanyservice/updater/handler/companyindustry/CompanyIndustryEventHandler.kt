package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import com.briolink.servicecompanyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.IndustryReadRepository
import com.briolink.servicecompanyservice.updater.service.SyncService
import org.springframework.transaction.annotation.Transactional

@Transactional
@EventHandler("IndustryCreatedEvent", "1.0")
class CompanyIndustryCreatedEventHandler(
    private val industryReadRepository: IndustryReadRepository,
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        industryReadRepository.save(
            IndustryReadEntity(
                id = event.data.id,
                name = event.data.name,
            ),
        )
    }
}

@EventHandler("IndustrySyncEvent", "1.0")
@Transactional
class IndustrySyncEventHandler(
    private val industryReadRepository: IndustryReadRepository,
    private val syncService: SyncService,
) : IEventHandler<IndustrySyncEvent> {
    override fun handle(event: IndustrySyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            industryReadRepository.save(
                IndustryReadEntity(
                    id = syncData.objectSync.id,
                    name = syncData.objectSync.name,
                ),
            )
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
        if (syncData.indexObjectSync == syncData.totalObjectSync) {
            println("COMPLETED COMPANY Industry")
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.CompanyIndustry)
        }
    }
}

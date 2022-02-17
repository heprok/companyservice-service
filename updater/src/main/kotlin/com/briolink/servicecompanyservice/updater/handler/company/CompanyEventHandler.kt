package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import com.briolink.servicecompanyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.updater.ReloadStatisticByCompanyId
import com.briolink.servicecompanyservice.updater.handler.companyservice.CompanyServiceHandlerService
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.servicecompanyservice.updater.service.SyncService
import org.springframework.context.ApplicationEventPublisher

@EventHandlers(
    EventHandler("CompanyCreatedEvent", "1.0"),
    EventHandler("CompanyUpdatedEvent", "1.0"),
)
class CompanyEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanyEvent> {
    override fun handle(event: CompanyEvent) {
        val updatedCompany = companyHandlerService.findById(event.data.id)
        val prevCountryId = updatedCompany?.data?.location?.country?.id
        val prevIndustryId = updatedCompany?.data?.industry?.id
        companyHandlerService.createOrUpdate(updatedCompany, event.data).let {
            if (event.name == "CompanyUpdatedEvent") {
                connectionHandlerService.updateCompany(it)
                companyServiceHandlerService.updateCompany(it)
                if (it.data.industry?.id != prevIndustryId || it.data.location?.country?.id != prevCountryId) {
                    applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(event.data.id))
                }
            }
        }
    }
}

@EventHandler("CompanySyncEvent", "1.0")
class CompanySyncEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val syncService: SyncService,
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<CompanySyncEvent> {
    override fun handle(event: CompanySyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            val updatedCompany = companyHandlerService.findById(syncData.objectSync.id)
            val prevCountryId = updatedCompany?.data?.location?.country?.id
            val prevIndustryId = updatedCompany?.data?.industry?.id
            companyHandlerService.createOrUpdate(updatedCompany, syncData.objectSync).let {
                connectionHandlerService.updateCompany(it)
                companyServiceHandlerService.updateCompany(it)
                if (it.data.industry?.id != prevIndustryId || it.data.location?.country?.id != prevCountryId) {
                    applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(syncData.objectSync.id))
                }
            }
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
            println("COMPLETED COMPANY")
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Company)
        }
    }
}

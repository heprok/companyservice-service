package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
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
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    syncService: SyncService,
) : SyncEventHandler<CompanySyncEvent>(ObjectSyncEnum.Company, syncService) {
    override fun handle(event: CompanySyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            val updatedCompany = companyHandlerService.findById(objectSync.id)
            val prevCountryId = updatedCompany?.data?.location?.country?.id
            val prevIndustryId = updatedCompany?.data?.industry?.id
            companyHandlerService.createOrUpdate(updatedCompany, objectSync).let {
                connectionHandlerService.updateCompany(it)
                companyServiceHandlerService.updateCompany(it)
                if (it.data.industry?.id != prevIndustryId || it.data.location?.country?.id != prevCountryId) {
                    applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(objectSync.id))
                }
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

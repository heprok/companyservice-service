package com.briolink.servicecompanyservice.updater.handler.statistic

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceStatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.servicecompanyservice.updater.ReloadStatisticByServiceId

@EventHandler("CompanyServiceStatisticRefreshEvent", "1.0")
class StatisticHandler(
    private val statisticHandlerService: StatisticHandlerService,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository
) : IEventHandler<CompanyServiceStatisticRefreshEvent> {
    override fun handle(event: CompanyServiceStatisticRefreshEvent) {
        val serviceUUID = event.data.serviceId.let {
            if (it == null) serviceReadRepository.getAllUUID() else listOf(it)
        }
        userPermissionRoleReadRepository.findAll().forEach {
            connectionReadRepository.changeVisibilityByCompanyIdAndUserId(companyId = it.accessObjectUuid, userId = it.userId, false)
        }
        serviceUUID.forEach {
            statisticHandlerService.refreshByService(ReloadStatisticByServiceId(it))
        }
    }
}

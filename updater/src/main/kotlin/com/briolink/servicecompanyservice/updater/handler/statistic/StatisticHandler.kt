package com.briolink.servicecompanyservice.updater.handler.statistic

import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceStatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.updater.ReloadStatisticByServiceId

@EventHandler("CompanyServiceStatisticRefreshEvent", "1.0")
class StatisticHandler(
    private val statisticHandlerService: StatisticHandlerService,
    private val serviceReadRepository: ServiceReadRepository
) : IEventHandler<CompanyServiceStatisticRefreshEvent> {
    override fun handle(event: CompanyServiceStatisticRefreshEvent) {
        val serviceUUID = event.data.serviceId.let {
            if (it == null) serviceReadRepository.getAllUUID() else listOf(it)
        }
        serviceUUID.forEach {
            statisticHandlerService.refreshByService(ReloadStatisticByServiceId(it))
        }
    }
}

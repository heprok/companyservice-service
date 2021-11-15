package com.briolink.servicecompanyservice.updater.handler.statistic

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionEvent

@EventHandler("StatisticRefreshEvent", "1.0")

class StatisticHandler(
    private val statisticHandlerService: StatisticHandlerService,
    private val serviceReadRepository: ServiceReadRepository
) : IEventHandler<StatisticRefreshEvent> {
    override fun handle(event: StatisticRefreshEvent) {
        val serviceIds = serviceReadRepository.findAll().map {
            it.id
        }
        serviceIds.forEach {
            statisticHandlerService.refreshByService(it)
        }
    }
}

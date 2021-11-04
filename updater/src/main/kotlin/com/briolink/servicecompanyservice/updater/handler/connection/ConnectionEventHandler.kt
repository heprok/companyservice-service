package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.servicecompanyservice.updater.handler.company.CompanyHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
        EventHandler("ConnectionCreatedEvent", "1.0"),
        EventHandler("ConnectionUpdatedEvent", "1.0"),
)
class ConnectionEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
//    private val statisticHandlerService: StatisticHandlerService
) : IEventHandler<ConnectionEvent> {
    override fun handle(event: ConnectionEvent) {
        val connection = event.data
        if (connection.status != ConnectionStatus.Draft && connection.status != ConnectionStatus.Rejected) {
            if (connection.participantFrom.companyRole!!.type == ConnectionCompanyRoleType.Buyer) {
                connection.participantTo = connection.participantFrom.also {
                    connection.participantFrom = connection.participantTo
                }
//                if (companyHandlerService.getPermission(
//                            userId = connection.participantFrom.userId!!,
//                            companyId = connection.participantFrom.companyId!!,
//                    ) != null &&
//                    companyHandlerService.getPermission(
//                            userId = connection.participantTo.userId!!,
//                            companyId = connection.participantTo.companyId!!,
//                    ) != null) {
                    connectionHandlerService.createOrUpdate(connection)
//                    statisticHandlerService.refreshByCompany(connection.participantTo.companyId!!)
//                    statisticHandlerService.refreshByCompany(connection.participantFrom.companyId!!)
//                }
            }
        } else if (connection.status == ConnectionStatus.Rejected) {
            connectionHandlerService.delete(connection.id)
        }
    }
}

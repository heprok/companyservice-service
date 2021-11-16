package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.servicecompanyservice.updater.handler.company.CompanyHandlerService
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.updater.handler.companyservice.CompanyServiceHandlerService

@EventHandlers(
        EventHandler("ConnectionCreatedEvent", "1.0"),
        EventHandler("ConnectionUpdatedEvent", "1.0"),
)
class ConnectionEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val companyServiceHandlerService: CompanyServiceHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
) : IEventHandler<ConnectionEvent> {
    override fun handle(event: ConnectionEvent) {
        val connection = event.data
        if (connection.status != ConnectionStatus.Draft && connection.status != ConnectionStatus.Rejected) {
            (connection.participantFrom.companyRole!!.type == ConnectionCompanyRoleType.Seller).let {
                if (it)
                    companyHandlerService.getPermission(connection.participantFrom.companyId!!, connection.participantFrom.userId!!) == null
                else
                    companyHandlerService.getPermission(connection.participantTo.companyId!!, connection.participantTo.userId!!) == null
            }.let { isHiddenConnection ->
                connectionHandlerService.createOrUpdate(connection, false)
            }
//                    statisticHandlerService.refreshByCompany(connection.participantTo.companyId!!)
//                    statisticHandlerService.refreshByCompany(connection.participantFrom.companyId!!)
//                }
        } else if (connection.status == ConnectionStatus.Rejected) {
            connectionHandlerService.delete(connection.id)
        }
    }
}

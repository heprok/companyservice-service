package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.servicecompanyservice.common.service.PermissionService
import com.briolink.servicecompanyservice.updater.handler.company.CompanyHandlerService

@EventHandlers(
    EventHandler("ConnectionCreatedEvent", "1.0"),
    EventHandler("ConnectionUpdatedEvent", "1.0"),
    EventHandler("CompanyConnectionEvent", "1.0"),
)
class ConnectionEventHandler(
    private val companyHandlerService: CompanyHandlerService,
    private val connectionHandlerService: ConnectionHandlerService,
    private val permissionService: PermissionService,
) : IEventHandler<ConnectionEvent> {
    override fun handle(event: ConnectionEvent) {
        val connection = event.data
        if (connection.status != ConnectionStatus.Rejected) {
            (connection.participantFrom.companyRole.type == ConnectionCompanyRoleType.Seller).let {
                if (it)
                    !permissionService.isHavePermission(
                        connection.participantFrom.companyId,
                        connection.participantFrom.userId,
                        AccessObjectTypeEnum.Company,
                        PermissionRightEnum.ConnectionCrud,
                    )
                else
                    !permissionService.isHavePermission(
                        connection.participantTo.companyId,
                        connection.participantTo.userId,
                        AccessObjectTypeEnum.Company,
                        PermissionRightEnum.ConnectionCrud,
                    )
            }.also { isHiddenConnection ->
                connectionHandlerService.createOrUpdate(connection, isHiddenConnection)
            }
        } else if (connection.status == ConnectionStatus.Rejected) {
            connectionHandlerService.delete(connection.id)
        }
    }
}

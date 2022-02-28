package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.servicecompanyservice.common.service.PermissionService
import com.briolink.servicecompanyservice.updater.handler.company.CompanyHandlerService
import com.briolink.servicecompanyservice.updater.service.SyncService

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
                        userId = connection.participantFrom.userId,
                        companyId = connection.participantFrom.companyId,
                        AccessObjectTypeEnum.Company,
                        PermissionRightEnum.ConnectionCrud,
                    )
                else
                    !permissionService.isHavePermission(
                        userId = connection.participantTo.userId,
                        companyId = connection.participantTo.companyId,
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

@EventHandler("CompanyConnectionChangeVisibilityEvent", "1.0")
class CompanyConnectionChangeVisibilityEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
) : IEventHandler<CompanyConnectionChangeVisibilityEvent> {
    override fun handle(event: CompanyConnectionChangeVisibilityEvent) {
        connectionHandlerService.setHiddenByCompanyId(
            companyId = event.data.companyId,
            connectionId = event.data.connectionId,
            hidden = event.data.hidden,
        )
    }
}

@EventHandler("CompanyConnectionDeletedEvent", "1.0")
class CompanyConnectionDeletedEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
) : IEventHandler<CompanyConnectionDeletedEvent> {
    override fun handle(event: CompanyConnectionDeletedEvent) {
        connectionHandlerService.softDeletedByCompanyId(companyId = event.data.companyId, connectionId = event.data.connectionId)
    }
}

@EventHandler("ConnectionSyncEvent", "1.0")
class ConnectionSyncEventHandler(
    private val connectionHandlerService: ConnectionHandlerService,
    private val permissionService: PermissionService,
    syncService: SyncService,
) : SyncEventHandler<ConnectionSyncEvent>(ObjectSyncEnum.Connection, syncService) {
    override fun handle(event: ConnectionSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val connection = syncData.objectSync!!
            if (connection.status != ConnectionStatus.Rejected) {
                (connection.participantFrom.companyRole.type == ConnectionCompanyRoleType.Seller).let {
                    if (it)
                        !permissionService.isHavePermission(
                            userId = connection.participantFrom.userId,
                            companyId = connection.participantFrom.companyId,
                            AccessObjectTypeEnum.Company,
                            PermissionRightEnum.ConnectionCrud,
                        )
                    else
                        !permissionService.isHavePermission(
                            userId = connection.participantTo.userId,
                            companyId = connection.participantTo.companyId,
                            AccessObjectTypeEnum.Company,
                            PermissionRightEnum.ConnectionCrud,
                        )
                }.also { isHiddenConnection ->
                    connectionHandlerService.createOrUpdate(connection, isHiddenConnection)
                }
            } else if (connection.status == ConnectionStatus.Rejected) {
                connectionHandlerService.delete(connection.id)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

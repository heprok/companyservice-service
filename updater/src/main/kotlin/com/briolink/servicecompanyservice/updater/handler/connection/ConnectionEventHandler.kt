package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.service.PermissionService
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.updater.service.SyncService

@EventHandlers(
    EventHandler("ConnectionCreatedEvent", "1.0"),
    EventHandler("ConnectionUpdatedEvent", "1.0"),
    EventHandler("CompanyConnectionEvent", "1.0"),
)
class ConnectionEventHandler(
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
                        accessObjectId = connection.participantFrom.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
                else
                    !permissionService.isHavePermission(
                        userId = connection.participantTo.userId,
                        accessObjectId = connection.participantTo.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
            }.also { isHiddenConnection ->
                connectionHandlerService.createOrUpdate(connection, isHiddenConnection)
            }
        } else connectionHandlerService.delete(connection.id)
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
                            accessObjectId = connection.participantFrom.companyId,
                            accessObjectType = AccessObjectTypeEnum.Company,
                            permissionRight = PermissionRightEnum.IsCanCreateProject,
                        )
                    else
                        !permissionService.isHavePermission(
                            userId = connection.participantTo.userId,
                            accessObjectId = connection.participantTo.companyId,
                            accessObjectType = AccessObjectTypeEnum.Company,
                            permissionRight = PermissionRightEnum.IsCanCreateProject,
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

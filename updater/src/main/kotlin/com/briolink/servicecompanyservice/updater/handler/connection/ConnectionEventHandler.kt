package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.enumeration.UpdaterEnum
import com.briolink.lib.sync.model.SyncError
import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.PermissionRightEnum
import com.briolink.servicecompanyservice.common.service.PermissionService
import com.briolink.servicecompanyservice.updater.handler.company.CompanyHandlerService
import com.briolink.servicecompanyservice.updater.service.SyncService
import org.springframework.context.ApplicationEventPublisher

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
    private val syncService: SyncService,
    private val permissionService: PermissionService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<ConnectionSyncEvent> {
    override fun handle(event: ConnectionSyncEvent) {
        val syncData = event.data
        if (syncData.indexObjectSync.toInt() == 1)
            syncService.startSync(syncData.syncId, syncData.service)
        try {
            val connection = syncData.objectSync
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
        if (syncData.indexObjectSync == syncData.totalObjectSync)
            syncService.completedObjectSync(syncData.syncId, syncData.service, ObjectSyncEnum.Connection)
    }
}

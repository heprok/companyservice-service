package com.briolink.servicecompanyservice.updater.handler.project

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
    EventHandler("ConnectionUpdatedEvent", "1.0")
)
class ProjectEventHandler(
    private val projectHandlerService: ProjectHandlerService,
    private val permissionService: PermissionService,
) : IEventHandler<ProjectEvent> {
    override fun handle(event: ProjectEvent) {
        val project = event.data
        if (project.status != ProjectStatus.Rejected) {
            (project.participantFrom.companyRole.type == ProjectCompanyRoleType.Seller).let {
                if (it)
                    !permissionService.isHavePermission(
                        userId = project.participantFrom.userId,
                        accessObjectId = project.participantFrom.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
                else
                    !permissionService.isHavePermission(
                        userId = project.participantTo.userId,
                        accessObjectId = project.participantTo.companyId,
                        accessObjectType = AccessObjectTypeEnum.Company,
                        permissionRight = PermissionRightEnum.IsCanCreateProject,
                    )
            }.also { isHiddenConnection ->
                projectHandlerService.createOrUpdate(project, isHiddenConnection)
            }
        } else projectHandlerService.delete(project.id)
    }
}

@EventHandler("CompanyConnectionChangeVisibilityEvent", "1.0")
class CompanyConnectionChangeVisibilityEventHandler(
    private val projectHandlerService: ProjectHandlerService,
) : IEventHandler<CompanyConnectionChangeVisibilityEvent> {
    override fun handle(event: CompanyConnectionChangeVisibilityEvent) {
        projectHandlerService.setHiddenByCompanyId(
            companyId = event.data.companyId,
            connectionId = event.data.connectionId,
            hidden = event.data.hidden,
        )
    }
}

@EventHandler("CompanyConnectionDeletedEvent", "1.0")
class CompanyConnectionDeletedEventHandler(
    private val projectHandlerService: ProjectHandlerService,
) : IEventHandler<CompanyConnectionDeletedEvent> {
    override fun handle(event: CompanyConnectionDeletedEvent) {
        projectHandlerService.softDeletedByCompanyId(companyId = event.data.companyId, connectionId = event.data.connectionId)
    }
}

@EventHandler("ProjectSyncEvent", "1.0")
class ProjectSyncEventHandler(
    private val projectHandlerService: ProjectHandlerService,
    private val permissionService: PermissionService,
    syncService: SyncService,
) : SyncEventHandler<ProjectSyncEvent>(ObjectSyncEnum.Connection, syncService) {
    override fun handle(event: ProjectSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val connection = syncData.objectSync!!
            if (connection.status != ProjectStatus.Rejected) {
                (connection.participantFrom.companyRole.type == ProjectCompanyRoleType.Seller).let {
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
                    projectHandlerService.createOrUpdate(connection, isHiddenConnection)
                }
            } else if (connection.status == ProjectStatus.Rejected) {
                projectHandlerService.delete(connection.id)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

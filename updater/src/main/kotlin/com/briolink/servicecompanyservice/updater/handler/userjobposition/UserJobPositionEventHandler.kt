package com.briolink.servicecompanyservice.updater.handler.userjobposition

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.service.PermissionService
import com.briolink.servicecompanyservice.updater.ReloadStatisticByCompanyId
import com.briolink.servicecompanyservice.updater.service.SyncService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@EventHandlers(
    EventHandler("UserJobPositionCreatedEvent", "1.0"),
    EventHandler("UserJobPositionUpdatedEvent", "1.0"),
)
@Transactional
@Service
class UserJobPositionCreatedEventHandler(
    private val connectionReadRepository: ConnectionReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val permissionService: PermissionService,
) : IEventHandler<UserJobPositionCreatedEvent> {
    override fun handle(event: UserJobPositionCreatedEvent) {
        val userJobPosition = event.data
        if (!permissionService.existsPermission(userId = userJobPosition.userId, accessObjectUuid = userJobPosition.companyId)) {
            permissionService.createPermission(
                userId = userJobPosition.userId,
                accessObjectUuid = userJobPosition.companyId,
                accessObjectType = AccessObjectTypeEnum.Company,
                roleType = UserPermissionRoleTypeEnum.Owner,
            )
            connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
                companyId = userJobPosition.companyId,
                userId = userJobPosition.userId, false,
            )
            applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(userJobPosition.companyId))
        }
    }
}

@EventHandler("UserJobPositionSyncEvent", "1.0")
class UserJobPositionSyncEventHandler(
    private val connectionReadRepository: ConnectionReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val permissionService: PermissionService,
    syncService: SyncService,
) : SyncEventHandler<UserJobPositionSyncEvent>(ObjectSyncEnum.UserJobPosition, syncService) {
    override fun handle(event: UserJobPositionSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val userJobPosition = syncData.objectSync!!
            if (!permissionService.existsPermission(userId = userJobPosition.userId, accessObjectUuid = userJobPosition.companyId)) {
                permissionService.createPermission(
                    userId = userJobPosition.userId,
                    accessObjectUuid = userJobPosition.companyId,
                    accessObjectType = AccessObjectTypeEnum.Company,
                    roleType = UserPermissionRoleTypeEnum.Owner,
                )
                connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
                    companyId = userJobPosition.companyId,
                    userId = userJobPosition.userId, false,
                )
                applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(userJobPosition.companyId))
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}
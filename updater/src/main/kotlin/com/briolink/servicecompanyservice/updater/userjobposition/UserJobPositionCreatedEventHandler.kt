package com.briolink.servicecompanyservice.updater.userjobposition

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.updater.ReloadStatisticByCompanyId
import com.briolink.servicecompanyservice.updater.handler.userpermission.AccessObjectType
import com.briolink.servicecompanyservice.updater.handler.userpermission.UserPermission
import com.briolink.servicecompanyservice.updater.handler.userpermission.UserPermissionHandlerService
import com.briolink.servicecompanyservice.updater.handler.userpermission.UserPermissionRoleType
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@EventHandlers(
    EventHandler("UserJobPositionCreatedEvent", "1.0"),
    EventHandler("UserJobPositionUpdatedEvent", "1.0")
)
class UserJobPositionCreatedEventHandler(
    private val userPermissionHandlerService: UserPermissionHandlerService,
    private val connectionReadRepository: ConnectionReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : IEventHandler<UserJobPositionCreatedEvent> {
    override fun handle(event: UserJobPositionCreatedEvent) {
        val userJobPosition = event.data
        println(event)
        println(event.name)
        println(event.data)
        userPermissionHandlerService.createOrUpdate(
            UserPermission(
                id = UUID.randomUUID(),
                userId = userJobPosition.userId,
                accessObjectUuid = userJobPosition.companyId,
                accessObjectType = AccessObjectType.Company,
                role = UserPermissionRoleType.Owner,
            ),
        )
        connectionReadRepository.changeVisibilityByCompanyIdAndUserId(
            companyId = userJobPosition.companyId,
            userId = userJobPosition.userId, false,
        )
        applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(userJobPosition.companyId))
    }
}

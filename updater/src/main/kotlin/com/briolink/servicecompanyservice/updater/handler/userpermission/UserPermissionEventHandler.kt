package com.briolink.servicecompanyservice.updater.handler.userpermission

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
        EventHandler("UserPermissionCreatedEvent", "1.0"),
        EventHandler("UserPermissionUpdatedEvent", "1.0"),
)
class UserPermissionEventHandler(
    private val userPermissionHandlerService: UserPermissionHandlerService
) : IEventHandler<UserPermissionEvent> {
    override fun handle(event: UserPermissionEvent) {
        val permission = event.data
        if(permission.accessObjectType == AccessObjectType.Company || permission.accessObjectType == AccessObjectType.CompanyService)
            userPermissionHandlerService.createOrUpdate(event.data)
    }
}

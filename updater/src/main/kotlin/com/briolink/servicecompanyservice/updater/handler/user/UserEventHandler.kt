package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionHandlerService

@EventHandlers(
    EventHandler("UserCreatedEvent", "1.0"),
    EventHandler("UserUpdatedEvent", "1.0"),
)
class UserEventHandler(
    private val UserHandlerService: UserHandlerService,
    private val connectionHandlerService: ConnectionHandlerService
) : IEventHandler<UserEvent> {
    override fun handle(event: UserEvent) {
        UserHandlerService.createOrUpdate(event.data).also {
            if (event.name == "UserUpdatedEvent") {
                connectionHandlerService.updateUser(it)
            }
        }
    }
}

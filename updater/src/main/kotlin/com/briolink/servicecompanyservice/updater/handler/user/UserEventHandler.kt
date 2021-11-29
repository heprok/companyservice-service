package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers

@EventHandlers(
    EventHandler("UserCreatedEvent", "1.0"),
    EventHandler("UserUpdatedEvent", "1.0"),
)
class UserCreatedEventHandler(
    private val UserHandlerService: UserHandlerService
) : IEventHandler<UserEvent> {
    override fun handle(event: UserEvent) {
        UserHandlerService.createOrUpdate(event.data)
    }
}

package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.updater.handler.user.UserEvent
import com.briolink.servicecompanyservice.updater.handler.user.UserHandlerService

@EventHandlers(
        EventHandler("UserCreatedEvent", "1.0"),
        EventHandler("UserUpdatedEvent", "1.0"),
)
class UserCreatedEventHandler(
    private val companyHandlerService: UserHandlerService
) : IEventHandler<UserEvent> {
    override fun handle(event: UserEvent) {
        companyHandlerService.createOrUpdate(event.data)
    }
}



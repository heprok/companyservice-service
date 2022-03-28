package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.lib.event.IEventHandler
import com.briolink.lib.event.annotation.EventHandler
import com.briolink.lib.event.annotation.EventHandlers
import com.briolink.lib.sync.SyncEventHandler
import com.briolink.lib.sync.enumeration.ObjectSyncEnum
import com.briolink.servicecompanyservice.updater.handler.project.ProjectHandlerService
import com.briolink.servicecompanyservice.updater.service.SyncService

@EventHandlers(
    EventHandler("UserCreatedEvent", "1.0"),
    EventHandler("UserUpdatedEvent", "1.0"),
)
class UserEventHandler(
    private val UserHandlerService: UserHandlerService,
    private val projectHandlerService: ProjectHandlerService
) : IEventHandler<UserEvent> {
    override fun handle(event: UserEvent) {
        UserHandlerService.createOrUpdate(event.data).also {
            if (event.name == "UserUpdatedEvent") {
                projectHandlerService.updateUser(it)
            }
        }
    }
}

@EventHandler("UserSyncEvent", "1.0")
class UserSyncEventHandler(
    private val userHandlerService: UserHandlerService,
    private val projectHandlerService: ProjectHandlerService,
    syncService: SyncService,
) : SyncEventHandler<UserSyncEvent>(ObjectSyncEnum.User, syncService) {
    override fun handle(event: UserSyncEvent) {
        val syncData = event.data
        if (!objectSyncStarted(syncData)) return
        try {
            val objectSync = syncData.objectSync!!
            userHandlerService.createOrUpdate(objectSync).also {
                projectHandlerService.updateUser(it)
            }
        } catch (ex: Exception) {
            sendError(syncData, ex)
        }
        objectSyncCompleted(syncData)
    }
}

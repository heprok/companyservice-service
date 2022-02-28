package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class UserEvent(override val data: UserEventData) : Event<UserEventData>("1.0")
data class UserSyncEvent(override val data: SyncData<UserEventData>) : SyncEvent<UserEventData>("1.0")
data class UserEventData(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("slug")
    var slug: String,
    @JsonProperty("firstName")
    val firstName: String,
    @JsonProperty("lastName")
    val lastName: String,
    @JsonProperty("image")
    val image: URL? = null,
)

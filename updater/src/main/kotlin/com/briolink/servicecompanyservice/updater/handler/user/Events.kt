package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.Event
import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class UserEvent(override val data: UserEventData) : Event<UserEventData>("1.0")
data class UserSyncEvent(override val data: UserEventSyncData) : Event<UserEventSyncData>("1.0")
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

data class UserEventSyncData(
    override val indexObjectSync: Long,
    override val service: ServiceEnum,
    override val syncId: Int,
    override val totalObjectSync: Long,
    override val objectSync: UserEventData,
) : ISyncData<UserEventData>

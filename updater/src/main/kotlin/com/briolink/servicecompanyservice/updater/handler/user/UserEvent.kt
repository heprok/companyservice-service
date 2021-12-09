package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.Event
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class UserEvent(override val data: User) : Event<User>("1.0")

data class User(
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

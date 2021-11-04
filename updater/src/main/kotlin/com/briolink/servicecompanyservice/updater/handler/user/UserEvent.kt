package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.event.Event

data class UserEvent(override val data: User) : Event<User>("1.0")


package com.briolink.servicecompanyservice.updater.userjobposition

import com.briolink.event.Event

data class UserJobPositionCreatedEvent(override val data: UserJobPosition) : Event<UserJobPosition>("1.0")

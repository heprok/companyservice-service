package com.briolink.servicecompanyservice.updater.event

import com.briolink.servicecompanyservice.updater.dto.Connection
import com.briolink.event.Event

data class ConnectionCreatedEvent(override val data: Connection) : Event<Connection>("1.0")
data class ConnectionUpdatedEvent(override val data: Connection) : Event<Connection>("1.0")

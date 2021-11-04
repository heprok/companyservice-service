package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.event.Event

data class ConnectionEvent(override val data: Connection) : Event<Connection>("1.0")

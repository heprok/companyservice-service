package com.briolink.servicecompanyservice.updater.handler.userpermission

import com.briolink.event.Event
data class UserPermissionEvent(override val data: UserPermission) : Event<UserPermission>("1.0")

package com.briolink.servicecompanyservice.updater.handler.userpermission

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*


enum class AccessObjectType(val value: Int) {
    @JsonProperty("1")
    Company(1),
    @JsonProperty("2")
    CompanyService(2),
}

enum class UserPermissionRoleType(val value: Int) {
    @JsonProperty("0")
    Employee(0),
    @JsonProperty("1")
    Owner(1)
}

data class UserPermission(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("accessObjectUuid")
    val accessObjectUuid: UUID,
    @JsonProperty("accessObjectType")
    val accessObjectType: AccessObjectType,
    @JsonProperty("role")
    val role: UserPermissionRoleType,
)

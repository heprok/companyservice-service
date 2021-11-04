package com.briolink.servicecompanyservice.common.jpa.projection

import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import java.util.*

interface CollaboratorRoleProjection {
    val id: UUID
    val name: String
}

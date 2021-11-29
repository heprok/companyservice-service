package com.briolink.servicecompanyservice.common.jpa.projection

import java.util.UUID

interface CollaboratorProjection {
    val id: UUID
    val name: String
}

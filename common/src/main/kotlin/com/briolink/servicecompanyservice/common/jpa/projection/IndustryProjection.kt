package com.briolink.servicecompanyservice.common.jpa.projection

import java.util.UUID

interface IndustryProjection {
    val id: UUID
    val name: String
}

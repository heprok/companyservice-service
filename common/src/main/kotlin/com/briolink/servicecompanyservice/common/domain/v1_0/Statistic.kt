package com.briolink.servicecompanyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Statistic(
    @JsonProperty
    val serviceId: UUID?
) : Domain

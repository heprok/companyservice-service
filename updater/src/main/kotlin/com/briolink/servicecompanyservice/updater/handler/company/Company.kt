package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.servicecompanyservice.common.jpa.dto.location.LocationId
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.*

data class Company(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val slug: String,
    @JsonProperty
    val locationId: LocationId? = null,
    @JsonProperty
    val industry: Industry? = null,
    @JsonProperty
    val logo: URL? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Industry(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
)

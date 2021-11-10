package com.briolink.servicecompanyservice.updater.handler.company

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.*

data class Company(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("location")
    val location: String? = null,
    @JsonProperty("industry")
    val industry: Industry? = null,
    @JsonProperty("logo")
    val logo: URL? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Industry(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
)

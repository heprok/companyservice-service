package com.briolink.servicecompanyservice.updater.dto

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
    @JsonProperty("logo")
    val logo: URL? = null,
)

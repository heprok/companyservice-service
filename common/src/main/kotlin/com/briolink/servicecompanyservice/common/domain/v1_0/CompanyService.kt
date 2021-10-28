package com.briolink.servicecompanyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.*

data class CompanyService (
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("companyId")
    val companyId: UUID,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("slug")
    val slug: String,
    @JsonProperty("price")
    val price: Double? = null,
    @JsonProperty("logo")
    val logo: URL? = null,
    @JsonProperty("description")
    val description: String? = null,
    @JsonProperty("created")
    val created: Instant,
    @JsonProperty("lastUsed")
    val lastUsed: LocalDate? = null,
    @JsonProperty("verifiedUses")
    val verifiedUses: Int = 0,
) : Domain

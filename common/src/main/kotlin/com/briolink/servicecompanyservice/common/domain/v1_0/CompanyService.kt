package com.briolink.servicecompanyservice.common.domain.v1_0

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class CompanyService(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val name: String,
    @JsonProperty
    val slug: String,
    @JsonProperty
    val price: Double? = null,
    @JsonProperty
    val logo: URL? = null,
    @JsonProperty
    val description: String? = null,
    @JsonProperty
    val created: Instant,
    @JsonProperty
    val lastUsed: LocalDate? = null
) : Domain

data class CompanyServiceDeletedData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val affectedConnections: ArrayList<UUID>
) : Domain

data class CompanyServiceHideData(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val companyId: UUID,
    @JsonProperty
    val hidden: Boolean,
    @JsonProperty
    val affectedConnections: ArrayList<UUID>
) : Domain

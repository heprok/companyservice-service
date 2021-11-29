package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
data class CompanyIndustry(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
)

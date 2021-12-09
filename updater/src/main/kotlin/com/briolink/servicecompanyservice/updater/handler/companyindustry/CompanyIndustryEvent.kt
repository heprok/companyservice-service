package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.Event
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class IndustryCreatedEvent(override val data: CompanyIndustry) : Event<CompanyIndustry>("1.0")

data class CompanyIndustry(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
)

package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class IndustryCreatedEvent(override val data: CompanyIndustryEventData) : Event<CompanyIndustryEventData>("1.0")
data class IndustrySyncEvent(override val data: SyncData<CompanyIndustryEventData>) : SyncEvent<CompanyIndustryEventData>("1.0")

data class CompanyIndustryEventData(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
)

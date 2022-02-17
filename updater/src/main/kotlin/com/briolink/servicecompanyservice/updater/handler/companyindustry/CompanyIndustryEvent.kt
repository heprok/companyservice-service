package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.Event
import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class IndustryCreatedEvent(override val data: CompanyIndustryEventData) : Event<CompanyIndustryEventData>("1.0")
data class IndustrySyncEvent(override val data: CompanyIndustryEventSyncData) : Event<CompanyIndustryEventSyncData>("1.0")

data class CompanyIndustryEventData(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("name")
    val name: String,
)

data class CompanyIndustryEventSyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: CompanyIndustryEventData
) : ISyncData<CompanyIndustryEventData>

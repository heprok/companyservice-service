package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.Event
import com.briolink.lib.location.model.LocationId
import com.briolink.lib.sync.ISyncData
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class CompanyEvent(override val data: CompanyEventData) : Event<CompanyEventData>("1.0")
data class CompanySyncEvent(override val data: CompanyEventSyncData) : Event<CompanyEventSyncData>("1.0")
data class CompanyEventData(
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

data class Industry(
    @JsonProperty
    val id: UUID,
    @JsonProperty
    val name: String,
)

data class CompanyEventSyncData(
    @JsonProperty
    override val indexObjectSync: Long,
    @JsonProperty
    override val service: ServiceEnum,
    @JsonProperty
    override val syncId: Int,
    @JsonProperty
    override val totalObjectSync: Long,
    @JsonProperty
    override val objectSync: CompanyEventData
) : ISyncData<CompanyEventData>

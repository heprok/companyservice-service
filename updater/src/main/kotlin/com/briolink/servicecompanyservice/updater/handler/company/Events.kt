package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.Event
import com.briolink.lib.location.model.LocationId
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.util.UUID

data class CompanyEvent(override val data: CompanyEventData) : Event<CompanyEventData>("1.0")
data class CompanySyncEvent(override val data: SyncData<CompanyEventData>) : SyncEvent<CompanyEventData>("1.0")
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

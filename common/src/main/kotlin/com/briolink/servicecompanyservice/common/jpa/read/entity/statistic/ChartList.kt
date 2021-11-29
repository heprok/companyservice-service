package com.briolink.servicecompanyservice.common.jpa.read.entity.statistic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

interface ChartListItem {
    val companyId: UUID
}

class ChartDataList<T>(
    @JsonProperty
    var name: String,
    @JsonProperty
    var items: MutableList<T>
)

@JsonIgnoreProperties(ignoreUnknown = true)
class ChartList<T> {
    @JsonProperty
    var data: MutableMap<String, ChartDataList<T>> = mutableMapOf()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithNumberOfUses(
    @JsonProperty override val companyId: UUID,
    @JsonProperty val companyRoles: MutableSet<String>,
    @JsonProperty val industry: String?,
    @JsonProperty var numberOfUses: Int
) : ChartListItem

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChartListItemWithDuration(
    @JsonProperty override val companyId: UUID,
    @JsonProperty var duration: Int
) : ChartListItem

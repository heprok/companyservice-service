package com.briolink.servicecompanyservice.common.dto.location

import com.briolink.servicecompanyservice.common.jpa.enumeration.LocationTypeEnum
import com.fasterxml.jackson.annotation.JsonProperty

class LocationId(
    @JsonProperty
    var id: Int,
    @JsonProperty
    var type: LocationTypeEnum,
) {
    companion object {
        fun fromString(typeAndId: String): LocationId {
            val attribute = typeAndId.split(";")
            return LocationId(
                id = attribute[1].toInt(),
                type = LocationTypeEnum.valueOf(attribute[0]),
            )
        }
    }
}

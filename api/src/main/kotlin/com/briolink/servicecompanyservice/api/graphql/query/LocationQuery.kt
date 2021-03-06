package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.lib.location.service.LocationService
import com.briolink.servicecompanyservice.api.types.Location
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class LocationQuery(
    private val locationService: LocationService
) {
    @DgsQuery
    fun getLocations(@InputArgument query: String?): List<Location> {
        return locationService.getSuggestionLocation(query = query)?.map {
            Location(id = it.locationId.toString(), name = it.name)
        } ?: listOf()
    }
}

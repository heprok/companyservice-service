package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.servicecompanyservice.api.types.Location
import com.briolink.servicecompanyservice.common.service.LocationService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class LocationQuery(
    private val locationService: LocationService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getLocations(@InputArgument("query") query: String): List<Location> {
        return locationService.getLocations(query = query)?.map {
            Location(id = it.id, name = it.name)
        } ?: listOf()
    }
}

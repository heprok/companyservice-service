package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.servicecompanyservice.api.service.ConnectionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionMutation(
    val connectionService: ConnectionService
) {
    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun hiddenConnection(
        @InputArgument("connectionId") connectionId: String,
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("hidden") hidden: Boolean
    ): Boolean {
        return connectionService.hiddenConnectionAndServiceId(
            connectionId = UUID.fromString(connectionId),
            serviceId = UUID.fromString(serviceId),
            hidden = hidden,
        )
    }
}

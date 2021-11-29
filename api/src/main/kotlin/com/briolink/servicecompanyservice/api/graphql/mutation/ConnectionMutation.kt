package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.servicecompanyservice.api.service.ConnectionService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.UUID

@DgsComponent
class ConnectionMutation(
    val connectionService: ConnectionService
) {
    @DgsMutation
    fun isHideConnection(
        @InputArgument("connectionId") connectionId: String,
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("isHide") isHide: Boolean
    ): Boolean {
        return connectionService.hiddenConnectionAndServiceId(
            connectionId = UUID.fromString(connectionId),
            serviceId = UUID.fromString(serviceId),
            isHide = isHide,
        )
    }
}

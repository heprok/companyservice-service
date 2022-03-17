package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.servicecompanyservice.api.service.ConnectionService
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.util.SecurityUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionMutation(
    private val connectionService: ConnectionService,
    private val serviceCompanyService: ServiceCompanyService
) {
    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun hiddenConnection(
        @InputArgument("connectionId") connectionId: String,
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("hidden") hidden: Boolean
    ): Boolean {
        val companyId = serviceCompanyService.getCompanyIdByServiceId(UUID.fromString(serviceId)) ?: throw DgsEntityNotFoundException()
        if (!serviceCompanyService.isHavePermission(
                companyId = companyId,
                userId = SecurityUtil.currentUserAccountId,
                permissionRight = PermissionRightEnum.IsCanEditProject,
            )
        ) return false

        return connectionService.hiddenConnectionAndServiceId(
            connectionId = UUID.fromString(connectionId),
            serviceId = UUID.fromString(serviceId),
            hidden = hidden,
        )
    }
}

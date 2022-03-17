package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.servicecompanyservice.api.graphql.fromEntity
import com.briolink.servicecompanyservice.api.service.ConnectionService
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.Collaborator
import com.briolink.servicecompanyservice.api.types.Connection
import com.briolink.servicecompanyservice.api.types.ConnectionFilter
import com.briolink.servicecompanyservice.api.types.ConnectionList
import com.briolink.servicecompanyservice.api.types.ConnectionSort
import com.briolink.servicecompanyservice.api.types.Industry
import com.briolink.servicecompanyservice.api.util.SecurityUtil
import com.briolink.servicecompanyservice.common.util.StringUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ConnectionQuery(
    private val connectionService: ConnectionService,
    private val serviceCompanyService: ServiceCompanyService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnections(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ConnectionFilter,
        @InputArgument("sort") sort: ConnectionSort,
        @InputArgument("limit") limit: Int,
        @InputArgument("offset") offset: Int,
    ): ConnectionList {
        return if (connectionService.existsConnectionByService(serviceId = UUID.fromString(serviceId))
        ) {
            val securityFilter = if (
                serviceCompanyService.isHavePermission(
                    companyId = UUID.fromString(companyId),
                    userId = SecurityUtil.currentUserAccountId,
                    permissionRight = PermissionRightEnum.IsCanEditProject,
                    serviceId = UUID.fromString(serviceId),
                )
            ) filter else filter.copy(isHidden = false)

            val result = connectionService.findAll(
                serviceId = UUID.fromString(serviceId),
                companyId = UUID.fromString(companyId),
                sort = sort,
                filter = securityFilter,
                limit = limit,
                offset = offset,
            )
            ConnectionList(
                items = result.map { Connection.fromEntity(it) },
                totalItems = connectionService.count(
                    serviceId = UUID.fromString(serviceId),
                    companyId = UUID.fromString(companyId),
                    filter = filter,
                ).toInt(),
            )
        } else ConnectionList(items = listOf(), totalItems = -1)
    }

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionsCount(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String,
        @InputArgument("filter") filter: ConnectionFilter
    ): Int =
        connectionService.count(serviceId = UUID.fromString(serviceId), companyId = UUID.fromString(companyId), filter = filter).toInt()

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionCollaborators(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String,
        @InputArgument("query") query: String,
    ): List<Collaborator> = connectionService.getCollaboratorsUsedForCompany(
        serviceId = UUID.fromString(serviceId),
        companyId = UUID.fromString(companyId),
        query = StringUtil.replaceNonWord(query),
    )

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getConnectionIndustries(
        @InputArgument("query") query: String,
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("companyId") companyId: String
    ): List<Industry> =
        connectionService.getIndustriesInConnectionFromCompany(
            serviceId = UUID.fromString(serviceId),
            companyId = UUID.fromString(companyId),
            query = StringUtil.replaceNonWord(query),
        ).map {
            Industry.fromEntity(it)
        }
}

package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.lib.permission.model.PermissionRight
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
import java.util.UUID

@DgsComponent
class ConnectionQuery(
    private val connectionService: ConnectionService,
    private val serviceCompanyService: ServiceCompanyService
) {
    @DgsQuery
    fun getConnections(
        @InputArgument serviceId: String,
        @InputArgument companyId: String,
        @InputArgument filter: ConnectionFilter,
        @InputArgument sort: ConnectionSort,
        @InputArgument limit: Int?,
        @InputArgument offset: Int?,
    ): ConnectionList {
        return if (connectionService.existsConnectionByService(serviceId = UUID.fromString(serviceId))
        ) {
            val securityFilter = if (!SecurityUtil.isGuest &&
                serviceCompanyService.isHavePermission(
                        companyId = UUID.fromString(companyId),
                        userId = SecurityUtil.currentUserAccountId,
                        right = PermissionRight("Company", "EditProject"),
                        serviceId = UUID.fromString(serviceId),
                    )
            ) filter else filter.copy(isHidden = false)

            val result = connectionService.findAll(
                serviceId = UUID.fromString(serviceId),
                companyId = UUID.fromString(companyId),
                sort = sort,
                filter = securityFilter,
                limit = limit ?: 10,
                offset = offset ?: 0,
            )
            ConnectionList(
                items = result.map { Connection.fromEntity(it) },
                totalItems = connectionService.count(
                    serviceId = UUID.fromString(serviceId),
                    companyId = UUID.fromString(companyId),
                    filter = securityFilter,
                ).toInt(),
            )
        } else ConnectionList(items = listOf(), totalItems = -1)
    }

    @DgsQuery
    fun getConnectionsCount(
        @InputArgument serviceId: String,
        @InputArgument companyId: String,
        @InputArgument filter: ConnectionFilter
    ): Int =
        connectionService.count(serviceId = UUID.fromString(serviceId), companyId = UUID.fromString(companyId), filter = filter).toInt()

    @DgsQuery
    fun getConnectionCollaborators(
        @InputArgument serviceId: String,
        @InputArgument companyId: String,
        @InputArgument query: String,
    ): List<Collaborator> = connectionService.getCollaboratorsUsedForCompany(
        serviceId = UUID.fromString(serviceId),
        companyId = UUID.fromString(companyId),
        query = StringUtil.replaceNonWord(query),
    )

    @DgsQuery
    fun getConnectionIndustries(
        @InputArgument query: String,
        @InputArgument serviceId: String,
        @InputArgument companyId: String
    ): List<Industry> =
        connectionService.getIndustriesInConnectionFromCompany(
            serviceId = UUID.fromString(serviceId),
            companyId = UUID.fromString(companyId),
            query = StringUtil.replaceNonWord(query),
        ).map {
            Industry.fromEntity(it)
        }
}

package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.servicecompanyservice.api.graphql.fromEntity
import com.briolink.servicecompanyservice.api.graphql.fromModel
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.Service
import com.briolink.servicecompanyservice.api.types.ServiceAndUserRole
import com.briolink.servicecompanyservice.api.types.UserPermission
import com.briolink.servicecompanyservice.api.util.SecurityUtil
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import java.util.UUID

@DgsComponent
class ServiceQuery(
    private val serviceCompanyService: ServiceCompanyService
) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getService(@InputArgument("slug") slug: String): ServiceAndUserRole {
        val service = serviceCompanyService.getServiceBySlug(slug).orElseThrow { throw DgsEntityNotFoundException() }

        val role = service?.let {
            serviceCompanyService.getPermissionRight(
                userId = SecurityUtil.currentUserAccountId,
                companyId = service.companyId,
                serviceId = service.id,
            )
        }
        return ServiceAndUserRole(
            service = Service.fromEntity(service),
            userPermission = role?.let { UserPermission.fromModel(role) },
        )
    }

    @DgsQuery
    @PreAuthorize("@servletUtil.isIntranet()")
    fun getServiceById(@InputArgument("id") id: String): Service =
        serviceCompanyService.findById(UUID.fromString(id)).orElseThrow { throw DgsEntityNotFoundException() }
            .let { Service.fromEntity(it) }

    @DgsQuery
    @PreAuthorize("@servletUtil.isIntranet()")
    fun countByCompanyId(@InputArgument("companyId") companyId: String): Int =
        serviceCompanyService.countByCompanyId(UUID.fromString(companyId)).toInt()
}

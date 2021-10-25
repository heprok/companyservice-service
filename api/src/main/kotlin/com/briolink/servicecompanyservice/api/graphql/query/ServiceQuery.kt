package com.briolink.servicecompanyservice.api.graphql.query

import com.briolink.servicecompanyservice.api.graphql.SecurityUtil
import com.briolink.servicecompanyservice.api.graphql.fromEntity
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.PermissionRole
import com.briolink.servicecompanyservice.api.types.Service
import com.briolink.servicecompanyservice.api.types.ServiceAndUserRole
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize

@DgsComponent
class ServiceQuery(private val serviceCompanyService: ServiceCompanyService) {
    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCompany(@InputArgument("slug") slug: String): ServiceAndUserRole {
        val service = serviceCompanyService.getServiceBySlug(slug).orElseThrow { throw DgsEntityNotFoundException() }
        val role = serviceCompanyService.getPermission(service.id, SecurityUtil.currentUserAccountId)
        return ServiceAndUserRole(
                service = Service.fromEntity(service),
                role = when (role) {
                    UserPermissionRoleReadEntity.RoleType.Employee -> PermissionRole.Employee
                    UserPermissionRoleReadEntity.RoleType.Owner -> PermissionRole.Owner
                    else -> null
                },
        )

    }
}

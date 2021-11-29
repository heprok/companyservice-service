package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.servicecompanyservice.common.service.LocationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val locationService: LocationService,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {

    fun createOrUpdate(company: Company): CompanyReadEntity {
        companyReadRepository.findById(company.id).orElse(CompanyReadEntity(company.id)).apply {
            name = company.name
            data = CompanyReadEntity.Data(
                slug = company.slug,
                logo = company.logo,
                industry = company.industry?.let { CompanyReadEntity.Industry(id = it.id, name = company.industry.name) },
            ).apply {
                location = company.locationId?.let { locationService.getLocation(it) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleTypeEnum) {
        userPermissionRoleReadRepository.save(
            userPermissionRoleReadRepository.getUserPermissionRole(
                accessObjectUuid = companyId,
                accessObjectType = AccessObjectTypeEnum.Company.value,
                userId = userId,
            )?.apply {
                role = roleType
            } ?: UserPermissionRoleReadEntity().apply {
                role = roleType
                accessObjectUuid = companyId
                this.userId = userId
                accessObjectType = AccessObjectTypeEnum.Company
            },
        )
    }

    fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleTypeEnum? {
        return userPermissionRoleReadRepository.getUserPermissionRole(
            accessObjectUuid = companyId,
            accessObjectType = AccessObjectTypeEnum.Company.value,
            userId = userId,
        )?.role
    }
}

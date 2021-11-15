package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyService
import com.briolink.servicecompanyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class CompanyServiceHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {

    fun createOrUpdate(serviceCompany: CompanyService) {
        val company = companyReadRepository.findById(serviceCompany.companyId)
                .orElseThrow { throw EntityNotFoundException(serviceCompany.companyId.toString() + " company not found") }
        serviceReadRepository.findById(serviceCompany.id)
                .orElse(ServiceReadEntity(id = serviceCompany.id, slug = serviceCompany.slug, companyId = serviceCompany.companyId))
                .apply {
                    data = ServiceReadEntity.Data(
                            name = serviceCompany.name,
                            description = serviceCompany.description,
                            logo = serviceCompany.logo,
                            price = serviceCompany.price,
                            created = serviceCompany.created,
                            company = ServiceReadEntity.Company(
                                    id = company.id,
                                    name = company.data.name,
                                    slug = company.data.slug,
                                    logo = company.data.logo,
                            ),
                    )
                    serviceReadRepository.save(this)
                }
    }

    fun updateCompany(company: CompanyReadEntity) {
        serviceReadRepository.updateCompany(
                companyId = company.id,
                name = company.data.name,
                slug = company.data.slug,
                logo = company.data.logo.toString(),
        )
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
                    accessObjectUuid = companyId
                    this.userId = userId
                    role = roleType
                    accessObjectType = AccessObjectTypeEnum.Company
                },
        )
    }

    fun getPermission(serviceId: UUID, userId: UUID): UserPermissionRoleTypeEnum? {
        return userPermissionRoleReadRepository.getUserPermissionRole(
                accessObjectUuid = serviceId,
                accessObjectType = AccessObjectTypeEnum.CompanyService.value,
                userId = userId,
        )?.role
    }
}

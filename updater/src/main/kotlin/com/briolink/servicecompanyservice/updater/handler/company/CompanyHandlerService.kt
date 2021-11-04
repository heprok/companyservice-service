package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {

    fun createOrUpdate(company: Company) {
        val companyRead = companyReadRepository.findById(company.id).orElse(CompanyReadEntity(company.id)).apply {
            data = CompanyReadEntity.Data(
                    slug = company.slug,
                    logo = company.logo,
                    name = company.name,
                    industry = company.industry?.let { CompanyReadEntity.Industry(id = it.id, name = company.industry.name) },
            )
            companyReadRepository.save(this)
        }
        serviceReadRepository.findByCompanyId(company.id).forEach {
            it.data.company = ServiceReadEntity.Company(
                    id = companyRead.id,
                    name = companyRead.data.name,
                    slug = companyRead.data.slug,
                    logo = companyRead.data.logo,
                    industry = companyRead.data.industry,
            )
            serviceReadRepository.save(it)
        }

        fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleReadEntity.RoleType) {
            userPermissionRoleReadRepository.save(
                    userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                            accessObjectUuid = companyId,
                            userId = userId,
                    )?.apply {
                        role = roleType
                    } ?: UserPermissionRoleReadEntity(accessObjectUuid = companyId, userId = userId, role = roleType),
            )
        }

        fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity.RoleType? {
            return userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                    accessObjectUuid = companyId,
                    accessObjectType = 1,
                    userId = userId,
            )?.role
        }
    }
}

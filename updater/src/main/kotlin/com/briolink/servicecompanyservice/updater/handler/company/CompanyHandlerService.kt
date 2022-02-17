package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.lib.location.model.LocationMinInfo
import com.briolink.lib.location.service.LocationService
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.data.repository.findByIdOrNull
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

    fun createOrUpdate(entityPrevCompany: CompanyReadEntity? = null, companyDomain: CompanyEventData): CompanyReadEntity {
        val company = entityPrevCompany ?: CompanyReadEntity(companyDomain.id)
        company.apply {
            name = companyDomain.name
            data = CompanyReadEntity.Data(
                slug = companyDomain.slug,
                logo = companyDomain.logo,
                industry = companyDomain.industry?.let { CompanyReadEntity.Industry(id = it.id, name = companyDomain.industry.name) },
            ).apply {
                location = companyDomain.locationId?.let { locationService.getLocationInfo(it, LocationMinInfo::class.java) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun findById(companyId: UUID): CompanyReadEntity? = companyReadRepository.findByIdOrNull(companyId)
}

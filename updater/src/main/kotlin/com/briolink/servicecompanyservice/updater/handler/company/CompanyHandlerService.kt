package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.lib.location.model.LocationMinInfo
import com.briolink.lib.location.service.LocationService
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class CompanyHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val locationService: LocationService
) {

    fun createOrUpdate(entityPrevCompany: CompanyReadEntity? = null, companyEventData: CompanyEventData): CompanyReadEntity {
        val company = entityPrevCompany ?: CompanyReadEntity(companyEventData.id)
        company.apply {
            name = companyEventData.name
            data = CompanyReadEntity.Data(
                slug = companyEventData.slug,
                logo = companyEventData.logo,
                industry = companyEventData.industry?.let {
                    CompanyReadEntity.Industry(
                        id = it.id,
                        name = companyEventData.industry.name,
                    )
                },
            ).apply {
                location = companyEventData.locationId?.let { locationService.getLocationInfo(it, LocationMinInfo::class.java) }
            }
            return companyReadRepository.save(this)
        }
    }

    fun findById(companyId: UUID): CompanyReadEntity? = companyReadRepository.findByIdOrNull(companyId)
}

package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyIndustryReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyIndustryReadRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CompanyIndustryHandlerService(
    private val companyIndustryReadRepository: CompanyIndustryReadRepository,
) {

    fun createOrUpdate(industryData: CompanyIndustryEventData): CompanyIndustryReadEntity {
        val industry = companyIndustryReadRepository.findByIdOrNull(industryData.id)
            ?: CompanyIndustryReadEntity(industryData.id, industryData.name)

        industry.apply {
            name = industryData.name
            return companyIndustryReadRepository.save(this)
        }
    }
}

package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceEventData
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class CompanyServiceHandlerService(
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
) {

    fun createOrUpdate(serviceCompany: CompanyServiceEventData) {
        val company = companyReadRepository.findById(serviceCompany.companyId)
            .orElseThrow { throw EntityNotFoundException(serviceCompany.companyId.toString() + " company not found") }
        serviceReadRepository.findById(serviceCompany.id)
            .orElse(
                ServiceReadEntity(
                    id = serviceCompany.id,
                    slug = serviceCompany.slug,
                    companyId = serviceCompany.companyId,
                    data = ServiceReadEntity.Data(
                        name = serviceCompany.name,
                        description = serviceCompany.description,
                        logo = serviceCompany.logo,
                        price = serviceCompany.price,
                        created = serviceCompany.created,
                        company = ServiceReadEntity.Company(
                            id = company.id,
                            name = company.name,
                            slug = company.data.slug,
                            logo = company.data.logo,
                        ),
                    ),
                ),
            )
            .apply {
                data.apply {
                    name = serviceCompany.name
                    description = serviceCompany.description
                    logo = serviceCompany.logo
                    price = serviceCompany.price
                }

                serviceReadRepository.save(this)
            }
    }

    fun updateCompany(company: CompanyReadEntity) {
        serviceReadRepository.updateCompany(
            companyId = company.id,
            name = company.name,
            slug = company.data.slug,
            logo = company.data.logo?.toString(),
        )
    }

    fun deleteById(id: UUID) {
        serviceReadRepository.deleteById(id)
    }
}

package com.briolink.servicecompanyservice.updater.handler.companyservice

import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.event.annotation.EventHandlers
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import javax.persistence.EntityNotFoundException

@EventHandlers (
        EventHandler("CompanyServiceUpdatedEvent", "1.0"),
        EventHandler("CompanyServiceCreatedEvent", "1.0")
)
class ServiceUpdatedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val serviceCompany = event.data
        val company = companyReadRepository.findById(serviceCompany.companyId)
                .orElseThrow { throw EntityNotFoundException(serviceCompany.companyId.toString() + " company not found") }
        serviceReadRepository.save(
                serviceReadRepository.findById(serviceCompany.id)
                        .orElse(ServiceReadEntity(id = serviceCompany.id, slug = serviceCompany.slug, companyId = serviceCompany.companyId))
                        .apply {
                            data.apply {
                                    description = serviceCompany.description
                                    logo = serviceCompany.logo
                                    price = serviceCompany.price
                                    created = serviceCompany.created
                                    company.apply {
                                        data.name = company.data.name
                                        data.slug = company.data.slug
                                        data.logo = company.data.logo
                                    }
                            }
                        },
        )
    }
}

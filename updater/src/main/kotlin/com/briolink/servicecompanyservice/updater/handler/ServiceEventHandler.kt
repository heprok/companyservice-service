package com.briolink.servicecompanyservice.updater.handler

import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import javax.persistence.EntityNotFoundException

@EventHandler("CompanyServiceUpdatedEvent", "1.0")
class ServiceUpdatedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val eventData = event.data
        println(eventData)
        val company = companyReadRepository.findById(eventData.companyId)
                .orElseThrow { throw EntityNotFoundException(eventData.companyId.toString() + " company not found") }
        serviceReadRepository.save(
                serviceReadRepository.findById(eventData.id)
                        .orElseThrow { throw EntityNotFoundException(eventData.id.toString() + " company service not found") }
                        .apply {
                            data.apply {
                                    description = eventData.description
                                    logo = eventData.logo
                                    price = eventData.price
                                    created = eventData.created
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

@EventHandler("CompanyServiceCreatedEvent", "1.0")
class ServiceCreatedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository
) : IEventHandler<CompanyServiceCreatedEvent> {
    override fun handle(event: CompanyServiceCreatedEvent) {
        val eventData = event.data
        val company = companyReadRepository.findById(eventData.companyId)
                .orElseThrow { throw EntityNotFoundException(eventData.companyId.toString() + " company not found") }
        serviceReadRepository.save(
                ServiceReadEntity(
                        id = eventData.id,
                        slug = eventData.slug,
                        companyId = eventData.companyId,
                ).apply {
                    data = ServiceReadEntity.Data(
                            name = eventData.name,
                            logo = eventData.logo,
                            price = eventData.price,
                            description = eventData.description,
                            created = eventData.created,
                            company = ServiceReadEntity.Company(
                                    id = company.id,
                                    name = company.data.name,
                                    slug = company.data.slug,
                                    logo = company.data.logo,

                                    ),
                    )
                },
        )
    }
}


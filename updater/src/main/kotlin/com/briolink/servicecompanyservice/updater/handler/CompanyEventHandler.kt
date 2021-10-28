package com.briolink.servicecompanyservice.updater.handler

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.updater.event.CompanyCreatedEvent
import com.briolink.servicecompanyservice.updater.event.CompanyUpdatedEvent

@EventHandler("CompanyCreatedEvent", "1.0")
class CompanyCreatedEventHandler(
    private val companyReadRepository: CompanyReadRepository,
) : IEventHandler<CompanyCreatedEvent> {
    override fun handle(event: CompanyCreatedEvent) {
        val company = event.data
        companyReadRepository.save(
                CompanyReadEntity(
                        id = company.id,
                ).apply {
                    data = CompanyReadEntity.Data(
                            name = company.name,
                            logo = company.logo,
                            slug = company.slug,
                    )
                },
        )
    }
}

@EventHandler("CompanyUpdatedEvent", "1.0")
class CompanyUpdatedEventHandler(
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository
) : IEventHandler<CompanyUpdatedEvent> {
    override fun handle(event: CompanyUpdatedEvent) {
        val data = event.data
        val company = companyReadRepository.getById(data.id).apply {
            this.data.slug = data.slug
            this.data.logo = data.logo
            this.data.name = data.name
            companyReadRepository.save(this)
        }
        serviceReadRepository.findByCompanyId(company.id).forEach {
            it.data.company = ServiceReadEntity.Company(
                    id = company.id,
                    name = company.data.name,
                    slug = company.data.slug,
                    logo = company.data.logo,
            )
            serviceReadRepository.save(it)
        }
    }
}




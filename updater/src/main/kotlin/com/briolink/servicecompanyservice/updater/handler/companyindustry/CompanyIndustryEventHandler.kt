package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.IndustryReadRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
@EventHandler("IndustryCreatedEvent", "1.0")
class CompanyIndustryCreatedEventHandler(
    private val industryReadRepository: IndustryReadRepository,
) : IEventHandler<IndustryCreatedEvent> {
    override fun handle(event: IndustryCreatedEvent) {
        industryReadRepository.save(
            IndustryReadEntity(
                id = event.data.id,
                name = event.data.name,
            ),
        )
    }
}

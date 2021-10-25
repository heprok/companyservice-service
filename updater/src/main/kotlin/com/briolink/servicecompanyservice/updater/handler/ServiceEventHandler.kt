package com.briolink.servicecompanyservice.updater.handler

import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.event.IEventHandler
import com.briolink.event.annotation.EventHandler
import com.briolink.servicecompanyservice.common.event.v1_0.ServiceCompanyCreatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.time.LocalDate

//@EventHandler("ServiceUpdatedEvent", "1.0")
//class ServiceUpdatedEventHandler(private val companyReadRepository: CompanyReadRepository) : IEventHandler<CompanyUpdatedEvent> {
//    override fun handle(event: CompanyUpdatedEvent) {
//        val data = event.data
//
//        val entity: CompanyReadEntity = try {
//            companyReadRepository.getById(data.id!!)
//        } catch (e: EntityNotFoundException) {
//            throw EntityNotFoundException(e.message)
//        }
//
//        entity.slug = data.slug!!
//        entity.data.name = data.name
//        entity.data.website = data.website
//        entity.data.description = data.description
//        entity.data.logo = data.logo
//        entity.data.isTypePublic = data.isTypePublic
////        entity.data.country = data.country ?: entity.data.country
////        entity.data.state = data.state ?: entity.data.state
////        entity.data.city = data.city ?: entity.data.city
//        entity.data.location = data.location
//        entity.data.facebook = data.facebook
//        entity.data.twitter = data.twitter
//        entity.data.occupation = data.occupation?.let {
//            CompanyReadEntity.Occupation(it.id.toString(), it.name)
//        }
//        entity.data.industry = data.industry?.let {
//            CompanyReadEntity.Industry(it.id.toString(), it.name)
//        }
//        entity.data.keywords = data.keywords?.let { list ->
//            list.map {
//                CompanyReadEntity.Keyword(
//                        it.id,
//                        it.name,
//                )
//            }
//        } ?: entity.data.keywords
//        companyReadRepository.save(entity)
//    }
//}

@EventHandler("ServiceCompanyCreatedEvent", "1.0")
class ServiceCreatedEventHandler(
    private val serviceReadRepository: ServiceReadRepository,
) : IEventHandler<ServiceCompanyCreatedEvent> {
    override fun handle(event: ServiceCompanyCreatedEvent) {
        val service = event.data
        serviceReadRepository.save(
                ServiceReadEntity(
                        id = service.id,
                        slug = service.slug,
                        companyId = service.companyId,
                ).apply {
                    data = ServiceReadEntity.Data(
                            name = service.name,
                            logo = service.logo,
                            price = service.price,
                            created = service.created
                    )
                },
        )
    }
}


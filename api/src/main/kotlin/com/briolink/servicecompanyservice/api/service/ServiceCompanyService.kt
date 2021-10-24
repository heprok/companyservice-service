package com.briolink.servicecompanyservice.api.service

import com.briolink.servicecompanyservice.common.event.v1_0.ServiceCompanyCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.ServiceCompanyUpdatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import com.briolink.servicecompanyservice.common.jpa.write.repository.ServiceWriteRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class ServiceCompanyService(
    val applicationEventPublisher: ApplicationEventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service,
    private val serviceCompanyWriteRepository: ServiceWriteRepository,
) {
    val SERVICE_PROFILE_IMAGE_PATH = "uploads/service-company/profile-image"
    fun create(
        companyId: UUID,
        name: String,
        price: Double?,
        logoTempKey: String?,
        description: String?,
        created: Instant? = null,
        slug: String? = null
     ): ServiceWriteEntity {
        val service = serviceCompanyWriteRepository.save(
                ServiceWriteEntity(
                        companyId = companyId,
                        name = name,
                        created = created,
                        slug = slug,
                        description = description,
                        price = price,
                ).apply {
                    this.logo = logoTempKey?.let {
                        awsS3Service.moveFromTemp(it, SERVICE_PROFILE_IMAGE_PATH)
                    }
                },
        )
        applicationEventPublisher.publishEvent(ServiceCompanyCreatedEvent(service.toDomain()))
        return service;
    }

    fun update(
        id: UUID,
        name: String,
        price: Double?,
        description: String?,
        logoTempKey: String?,
    ): ServiceWriteEntity? =
            serviceCompanyWriteRepository.findByIdOrNull(id)?.let { writeEntity ->
                logoTempKey?.let {
                    writeEntity.logo =  awsS3Service.moveFromTemp(it, SERVICE_PROFILE_IMAGE_PATH)
                }
                writeEntity.name = name
                writeEntity.price = price
                writeEntity.description = description
                serviceCompanyWriteRepository.save(writeEntity)
                applicationEventPublisher.publishEvent(ServiceCompanyUpdatedEvent(writeEntity.toDomain()))
                writeEntity
            }

//    fun uploadProfileImage(id: UUID, image: MultipartFile?): URL? {
//        val service = serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
//        val imageUrl: URL? = if (image != null) awsS3Service.uploadImage(SERVICE_PROFILE_IMAGE_PATH, image) else null
//        service.logo = imageUrl
//        update(service)
//        return imageUrl
//    }
}

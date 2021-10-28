package com.briolink.servicecompanyservice.api.service

import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceUpdatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import com.briolink.servicecompanyservice.common.jpa.write.repository.ServiceWriteRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.Instant
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class ServiceCompanyService(
    val applicationEventPublisher: ApplicationEventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service,
    private val serviceCompanyWriteRepository: ServiceWriteRepository,
    private val serviceCompanyReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository
) {
    val SERVICE_PROFILE_IMAGE_PATH = "uploads/service-company/profile-image"
    fun create(
        companyId: UUID,
        name: String,
        price: Double?,
        logoTempKey: String?,
        description: String?,
        logo: URL?,
        created: Instant? = null,
     ): ServiceWriteEntity {
        val slugCompany = companyReadRepository.findById(companyId).orElseThrow { throw EntityNotFoundException("$companyId company not found") }.data.slug

        val service = serviceCompanyWriteRepository.save(
                ServiceWriteEntity(
                        companyId = companyId,
                        name = name,
                        created = created,
                        slug = StringUtil.slugify("$slugCompany $name"),
                        logo = logo,
                        description = description,
                        price = price,
                ).apply {
                    this.logo = logoTempKey?.let {
                        awsS3Service.moveFromTemp(it, SERVICE_PROFILE_IMAGE_PATH)
                    }
                },
        )
        applicationEventPublisher.publishEvent(CompanyServiceCreatedEvent(service.toDomain()))
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
                applicationEventPublisher.publishEvent(CompanyServiceUpdatedEvent(writeEntity.toDomain()))
                writeEntity
            }

    fun getServiceBySlug(slug: String): Optional<ServiceReadEntity> = serviceCompanyReadRepository.findBySlug(slug)
    fun getPermission(serviceId: UUID, userId: UUID): UserPermissionRoleReadEntity.RoleType? {
        return userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
                accessObjectUuid = serviceId,
                accessObjectType = 2,
                userId = userId,
        )?.role
    }

//    fun uploadProfileImage(id: UUID, image: MultipartFile?): URL? {
//        val service = serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
//        val imageUrl: URL? = if (image != null) awsS3Service.uploadImage(SERVICE_PROFILE_IMAGE_PATH, image) else null
//        service.logo = imageUrl
//        update(service)
//        return imageUrl
//    }
}

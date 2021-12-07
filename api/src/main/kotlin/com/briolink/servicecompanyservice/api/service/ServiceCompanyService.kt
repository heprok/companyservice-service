package com.briolink.servicecompanyservice.api.service

import com.briolink.event.publisher.EventPublisher
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceDeletedData
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceHideData
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceDeletedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceHideEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceUpdatedEvent
import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import com.briolink.servicecompanyservice.common.jpa.write.repository.ServiceWriteRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.util.Optional
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
@Transactional
class ServiceCompanyService(
    val eventPublisher: EventPublisher,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
    private val awsS3Service: AwsS3Service,
    private val serviceCompanyWriteRepository: ServiceWriteRepository,
    private val serviceCompanyReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val connectionReadRepository: ConnectionReadRepository
) {
    val SERVICE_PROFILE_IMAGE_PATH = "uploads/service-company/profile-image"
    fun create(
        companyId: UUID,
        name: String,
        price: Double? = null,
        description: String? = null,
        fileImage: MultipartFile? = null,
        logo: URL? = null,
        slug: String? = null,
        created: Instant? = null,
    ): ServiceWriteEntity {
        val nameCompany = companyReadRepository.findById(companyId)
            .orElseThrow { throw EntityNotFoundException("$companyId company not found") }.name
        serviceCompanyWriteRepository.save(
            ServiceWriteEntity(
                companyId = companyId,
                name = name,
                created = created,
                slug = StringUtil.slugify("$nameCompany $name ", true),
                description = description,
                price = price,
            ).apply {
                this.logo = logo ?: fileImage?.let { awsS3Service.uploadImage(SERVICE_PROFILE_IMAGE_PATH, it) }
            },
        ).let {
            eventPublisher.publish(CompanyServiceCreatedEvent(it.toDomain()))
            return it
        }
    }

    fun update(
        id: UUID,
        price: Double?,
        description: String?,
    ): ServiceWriteEntity? =
        serviceCompanyWriteRepository.findByIdOrNull(id)?.let { writeEntity ->
            writeEntity.price = price
            writeEntity.description = description
            serviceCompanyWriteRepository.save(writeEntity)
            serviceCompanyReadRepository.getById(id).apply {
                data.apply {
                    this.price = price
                    this.description = description
                }
                serviceCompanyReadRepository.save(this)
            }
            eventPublisher.publishAsync(CompanyServiceUpdatedEvent(writeEntity.toDomain()))
            writeEntity
        }

    fun update(
        entity: ServiceWriteEntity
    ): ServiceWriteEntity? {
        serviceCompanyWriteRepository.save(entity)
        eventPublisher.publishAsync(CompanyServiceUpdatedEvent(entity.toDomain()))
        return entity
    }

    fun getServiceBySlug(slug: String): Optional<ServiceReadEntity> = serviceCompanyReadRepository.findBySlug(slug)

    // TODO Выгесьи в сервис и изменить на company service
    fun getPermission(serviceId: UUID, userId: UUID): UserPermissionRoleTypeEnum? {
        return userPermissionRoleReadRepository.getUserPermissionRole(
            accessObjectUuid = serviceId,
            accessObjectType = AccessObjectTypeEnum.Company.value,
            userId = userId,
        )?.role
    }

    fun uploadProfileImage(id: UUID, image: MultipartFile?): URL? {
        val serviceWrite =
            serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
        val imageUrl: URL? = if (image != null) awsS3Service.uploadImage(SERVICE_PROFILE_IMAGE_PATH, image) else null
        serviceWrite.logo = imageUrl
        serviceCompanyReadRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
            .apply {
                data.apply {
                    logo = imageUrl
                }
                serviceCompanyReadRepository.save(this)
            }
        update(serviceWrite)
        return imageUrl
    }

    fun delete(id: UUID, deletedBy: UUID) {
        val affectedConnections = ArrayList(connectionReadRepository.getConnectionIdsAffectedByServiceId(serviceId = id))
        serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
            .apply {
                this.deleted = Instant.now()
                this.deletedBy = deletedBy
                serviceCompanyWriteRepository.save(this)
                eventPublisher.publishAsync(
                    CompanyServiceDeletedEvent(
                        CompanyServiceDeletedData(
                            id,
                            companyId,
                            affectedConnections = affectedConnections,
                        ),
                    ),
                )
            }
    }

    fun hide(id: UUID) {
        val affectedConnections = ArrayList(connectionReadRepository.getConnectionIdsAffectedByServiceId(serviceId = id))
        println(affectedConnections)
        serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
            .apply {
                this.hidden = true
                serviceCompanyWriteRepository.save(this)
                eventPublisher.publishAsync(
                    CompanyServiceHideEvent(
                        CompanyServiceHideData(
                            id = id,
                            companyId = this.companyId,
                            hidden = true,
                            affectedConnections = affectedConnections,
                        ),
                    ),
                )
            }
    }

    fun findById(serviceId: UUID): Optional<ServiceWriteEntity> = serviceCompanyWriteRepository.findById(serviceId)

    fun findByNameAndCompanyId(companyId: UUID, name: String): ServiceWriteEntity? {
        return serviceCompanyWriteRepository.findByCompanyIdAndName(companyId = companyId, name = name)
    }

    fun countByCompanyId(companyId: UUID): Long = serviceCompanyWriteRepository.countByCompanyId(companyId)
}

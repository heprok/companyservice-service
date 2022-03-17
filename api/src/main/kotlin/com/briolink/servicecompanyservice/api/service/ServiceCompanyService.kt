package com.briolink.servicecompanyservice.api.service

import com.briolink.lib.event.publisher.EventPublisher
import com.briolink.lib.permission.enumeration.AccessObjectTypeEnum
import com.briolink.lib.permission.enumeration.PermissionRightEnum
import com.briolink.lib.permission.model.UserPermissionRights
import com.briolink.lib.permission.service.PermissionService
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncUtil
import com.briolink.lib.sync.enumeration.ServiceEnum
import com.briolink.lib.sync.model.PeriodDateTime
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceDeletedData
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceHideData
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceCreatedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceDeletedEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceHideEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceSyncEvent
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceUpdatedEvent
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.runAfterTxCommit
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
    private val eventPublisher: EventPublisher,
    private val awsS3Service: AwsS3Service,
    private val serviceCompanyWriteRepository: ServiceWriteRepository,
    private val serviceCompanyReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val permissionService: PermissionService,
) {
    val SERVICE_PROFILE_IMAGE_PATH = "uploads/companyservice/profile-image"
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
                slug = StringUtil.slugify("$nameCompany $name ", true),
                description = description,
                price = price,
            ).apply {
                if (created != null) this.created = created
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
        serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
            .apply {
                this.deleted = Instant.now()
                this.deletedBy = deletedBy
                serviceCompanyWriteRepository.save(this)
                runAfterTxCommit {
                    eventPublisher.publishAsync(
                        CompanyServiceDeletedEvent(
                            CompanyServiceDeletedData(
                                id = id,
                                companyId = companyId,
                                slug = slug,
                            ),
                        ),
                    )
                }
            }
    }

    fun hide(id: UUID) {
        serviceCompanyWriteRepository.findById(id).orElseThrow { throw EntityNotFoundException("service with $id not found") }
            .apply {
                this.hidden = true
                serviceCompanyWriteRepository.save(this)
                runAfterTxCommit {
                    eventPublisher.publishAsync(
                        CompanyServiceHideEvent(
                            CompanyServiceHideData(
                                id = id,
                                companyId = this.companyId,
                                hidden = true,
                                slug = slug,
                            ),
                        ),
                    )
                }
            }
    }

    fun findById(serviceId: UUID): Optional<ServiceWriteEntity> = serviceCompanyWriteRepository.findById(serviceId)

    fun findByNameAndCompanyId(companyId: UUID, name: String): ServiceWriteEntity? {
        return serviceCompanyWriteRepository.findByCompanyIdAndName(companyId = companyId, name = name)
    }

    fun isHavePermission(companyId: UUID, userId: UUID, permissionRight: PermissionRightEnum, serviceId: UUID? = null): Boolean {
        return if (permissionService.isHavePermission(
                userId = userId,
                accessObjectId = companyId,
                accessObjectType = AccessObjectTypeEnum.Company,
                permissionRight = permissionRight,
            )
        ) true else serviceId?.let {
            permissionService.isHavePermission(
                userId = userId,
                accessObjectId = it,
                accessObjectType = AccessObjectTypeEnum.CompanyService,
                permissionRight = permissionRight,
            )
        } ?: false
    }

    fun getPermissionRight(companyId: UUID, userId: UUID, serviceId: UUID? = null): UserPermissionRights? {
        return permissionService.getUserPermissionRights(
            userId,
            companyId,
            AccessObjectTypeEnum.Company,
        ) ?: serviceId?.let {
            permissionService.getUserPermissionRights(
                userId,
                it,
                AccessObjectTypeEnum.CompanyService,
            )
        }
    }

    fun countByCompanyId(companyId: UUID): Long = serviceCompanyWriteRepository.countByCompanyId(companyId)

    private fun publishCompanyServiceSyncEvent(
        syncId: Int,
        objectIndex: Long,
        totalObjects: Long,
        entity: ServiceWriteEntity?
    ) {
        eventPublisher.publishAsync(
            CompanyServiceSyncEvent(
                SyncData(
                    objectIndex = objectIndex,
                    totalObjects = totalObjects,
                    objectSync = entity?.toDomain(),
                    syncId = syncId,
                    service = ServiceEnum.CompanyService,
                ),
            ),
        )
    }

    fun publishSyncEvent(syncId: Int, period: PeriodDateTime? = null) {
        SyncUtil.publishSyncEvent(period, serviceCompanyWriteRepository) { indexElement, totalElements, entity ->
            publishCompanyServiceSyncEvent(
                syncId, indexElement, totalElements,
                entity as ServiceWriteEntity?,
            )
        }
    }

    fun getCompanyIdByServiceId(serviceId: UUID): UUID? = serviceCompanyWriteRepository.getCompanyIdByServiceId(serviceId)
}

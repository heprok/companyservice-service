package com.briolink.servicecompanyservice.common.jpa.write.repository

import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ServiceWriteRepository : JpaRepository<ServiceWriteEntity, UUID> {
    fun findBySlug(slug: String): Optional<ServiceWriteEntity>
    fun existsBySlug(slug: String): Boolean
    fun findByCompanyIdAndName(companyId: UUID, name: String): ServiceWriteEntity?
}

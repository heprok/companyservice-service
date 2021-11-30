package com.briolink.servicecompanyservice.common.jpa.write.repository

import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface ServiceWriteRepository : JpaRepository<ServiceWriteEntity, UUID> {
    fun findBySlug(slug: String): Optional<ServiceWriteEntity>
    fun existsBySlug(slug: String): Boolean
    fun findByCompanyIdAndName(companyId: UUID, name: String): ServiceWriteEntity?

    @Query("SELECT count(s) FROM ServiceWriteEntity s WHERE c.deleted is null")
    fun countByCompanyId(companyId: UUID): Long
}

package com.briolink.servicecompanyservice.common.jpa.write.repository

import com.briolink.lib.sync.BaseTimeMarkRepository
import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.Optional
import java.util.UUID

interface ServiceWriteRepository : JpaRepository<ServiceWriteEntity, UUID>, BaseTimeMarkRepository<ServiceWriteEntity> {
    fun findBySlug(slug: String): Optional<ServiceWriteEntity>
    fun existsBySlug(slug: String): Boolean

    @Query("SELECT s FROM ServiceWriteEntity s WHERE s.companyId = ?1 and lower(s.name) = lower(?2)")
    fun findByCompanyIdAndName(companyId: UUID, name: String): ServiceWriteEntity?

    @Query("SELECT count(s) FROM ServiceWriteEntity s WHERE s.deleted is null AND s.companyId = ?1")
    fun countByCompanyId(companyId: UUID): Long

    @Query("SELECT s FROM ServiceWriteEntity s WHERE s.deleted is null")
    fun findAllNotDeleted(): List<ServiceWriteEntity>

    @Query("SELECT s from ServiceWriteEntity s WHERE s.created BETWEEN ?1 AND ?2 OR s.changed BETWEEN ?1 AND ?2")
    override fun findByPeriod(
        start: Instant,
        end: Instant,
        pageable: Pageable
    ): Page<ServiceWriteEntity>

    fun existsByNameAndCompanyId(name: String, companyId: UUID): Boolean
}

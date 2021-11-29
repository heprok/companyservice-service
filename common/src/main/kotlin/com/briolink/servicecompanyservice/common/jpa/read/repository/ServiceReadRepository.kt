package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface ServiceReadRepository : JpaRepository<ServiceReadEntity, UUID> {
    fun findByCompanyIdIs(companyId: UUID, pageable: Pageable? = null): Page<ServiceReadEntity>

    fun existsByCompanyId(companyId: UUID): Boolean
    fun findBySlug(slug: String): Optional<ServiceReadEntity>

//    @Modifying
//    @Query("UPDATE ServiceReadEntity s SET s.isHide = ?3 where s.id = ?1 and s.companyId = ?2")
//    fun hideServiceByIdAndCompanyId(id: UUID, companyId: UUID, isHide: Boolean)

    @Query("SELECT id FROM ServiceReadEntity")
    fun getAllUUID(): List<UUID>
    fun findByCompanyId(companyId: UUID): List<ServiceReadEntity>

    @Modifying
    @Query(
        """update ServiceReadEntity c
           set c.data = function('jsonb_sets', c.data,
                '{company,name}', :name, text,
                '{company,slug}', :slug, text,
                '{company,image}', :logo, text
           ) where c.companyId = :companyId""",
    )
    fun updateCompany(
        @Param("companyId") companyId: UUID,
        @Param("name") name: String,
        @Param("slug") slug: String,
        @Param("logo") logo: String
    )

    @Modifying
    @Query("DELETE from ServiceReadEntity c where c.id = ?1")
    override fun deleteById(id: UUID)
}

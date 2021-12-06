package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CompanyReadRepository : JpaRepository<CompanyReadEntity, UUID> {
    fun findByIdIsIn(ids: List<UUID>): List<CompanyReadEntity>

    @Query("SELECT id FROM CompanyReadEntity")
    fun getAllUUID(): List<UUID>
}

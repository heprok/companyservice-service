package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyIndustryReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CompanyIndustryReadRepository : JpaRepository<CompanyIndustryReadEntity, UUID>

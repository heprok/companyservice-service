package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.IndustryReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface IndustryReadRepository : JpaRepository<IndustryReadEntity, UUID> {
}

package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.StatisticReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StatisticReadRepository : JpaRepository<StatisticReadEntity, UUID> {
    fun findByServiceId(companyId: UUID): Optional<StatisticReadEntity>
}

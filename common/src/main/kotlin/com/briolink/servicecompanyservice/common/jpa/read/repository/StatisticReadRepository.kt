package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface StatisticReadRepository : JpaRepository<StatisticReadEntity, UUID> {
    fun findByServiceId(serviceId: UUID): StatisticReadEntity?
    @Modifying
    @Query("DELETE from StatisticReadEntity s where s.serviceId = ?1")
    fun deleteByServiceId(serviceId: UUID)
}

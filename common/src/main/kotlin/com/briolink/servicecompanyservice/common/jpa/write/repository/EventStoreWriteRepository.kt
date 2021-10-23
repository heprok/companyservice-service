package com.briolink.servicecompanyservice.common.jpa.write.repository

import com.briolink.servicecompanyservice.common.jpa.write.entity.EventStoreWriteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface EventStoreWriteRepository : JpaRepository<EventStoreWriteEntity, UUID>

package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.lib.sync.ISyncLogRepository
import com.briolink.lib.sync.SyncLogId
import com.briolink.servicecompanyservice.common.jpa.read.entity.SyncLogReadEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SyncLogReadRepository : JpaRepository<SyncLogReadEntity, SyncLogId>, ISyncLogRepository<SyncLogReadEntity>

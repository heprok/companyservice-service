package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.UserReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserReadRepository : JpaRepository<UserReadEntity, UUID> {
    fun findByIdIsIn(ids: List<UUID>): List<UserReadEntity>
}

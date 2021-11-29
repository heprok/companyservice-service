package com.briolink.servicecompanyservice.common.jpa.read.repository

import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserPermissionRoleReadRepository : JpaRepository<UserPermissionRoleReadEntity, UUID> {

    @Query(
        """
        SELECT c 
        FROM UserPermissionRoleReadEntity c
        WHERE 
            c.accessObjectUuid = :accessObjectUuid AND
            c.userId = :userId AND
            c._accessObjectType = :accessObjectType
    """,
    )
    fun getUserPermissionRole(
        @Param("accessObjectUuid") accessObjectUuid: UUID,
        @Param("accessObjectType") accessObjectType: Int = 1,
        @Param("userId") userId: UUID
    ): UserPermissionRoleReadEntity?
}

package com.briolink.servicecompanyservice.updater.handler.userpermission

import com.briolink.servicecompanyservice.common.jpa.enumeration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserPermissionHandlerService(
    private val userPermissionReadRepository: UserPermissionRoleReadRepository
) {
    fun createOrUpdate(userPermission: UserPermission) {
        UserPermissionRoleReadEntity(userPermission.id).apply {
            userId = userPermission.userId
            accessObjectUuid = userPermission.accessObjectUuid
            accessObjectType = AccessObjectTypeEnum.valueOf(userPermission.accessObjectType.name)
            role = UserPermissionRoleTypeEnum.valueOf(userPermission.role.name)
            userPermissionReadRepository.save(this)
        }
    }
}

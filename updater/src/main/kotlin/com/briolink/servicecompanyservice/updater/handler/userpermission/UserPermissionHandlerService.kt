package com.briolink.servicecompanyservice.updater.handler.userpermission

import com.briolink.servicecompanyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository

class UserPermissionHandlerService (
    private val userPermissionReadRepository: UserPermissionRoleReadRepository
        ) {
    fun createOrUpdate(userPermission: UserPermission) {
        userPermissionReadRepository.findById(userPermission.id).orElse( UserPermissionRoleReadEntity(userPermission.id)).apply {
            userId = userPermission.userId
            accessObjectUuid = userPermission.accessObjectUuid
            accessObjectType = AccessObjectTypeEnum.valueOf(userPermission.accessObjectType.name)
            role = UserPermissionRoleTypeEnum.valueOf(userPermission.role.name)
            userPermissionReadRepository.save(this)
        }
    }
}

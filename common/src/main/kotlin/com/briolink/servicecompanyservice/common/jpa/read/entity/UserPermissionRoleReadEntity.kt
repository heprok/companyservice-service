package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.briolink.servicecompanyservice.common.jpa.enumration.AccessObjectTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.write.entity.BaseWriteEntity
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "user_permission_role", schema = "read")
@Entity
class UserPermissionRoleReadEntity(
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Type(type = "pg-uuid")
    @Column(name = "access_object_uuid", nullable = false)
    var accessObjectUuid: UUID,

    @Column(name = "access_object_type", nullable = false)
    private var _accessObjectType: Int = AccessObjectTypeEnum.CompanyService.value,

    @Type(type = "pg-uuid")
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "role", nullable = false)
    private var _role: Int = UserPermissionRoleTypeEnum.Employee.value

) : BaseReadEntity() {
    var accessObjectType: AccessObjectTypeEnum
        get() = AccessObjectTypeEnum.fromInt(_accessObjectType)
        set(value) {
            _accessObjectType = value.value
        }

    var role: UserPermissionRoleTypeEnum
        get() = UserPermissionRoleTypeEnum.fromInt(_role)
        set(value) {
            _role = value.value
        }

}

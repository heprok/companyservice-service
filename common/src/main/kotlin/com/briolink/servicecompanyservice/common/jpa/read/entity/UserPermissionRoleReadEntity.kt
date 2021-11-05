package com.briolink.servicecompanyservice.common.jpa.read.entity

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
    @Type(type="pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Type(type="pg-uuid")
    @Column(name = "access_object_uuid", nullable = false, columnDefinition="uuid")
    var accessObjectUuid: UUID,

    @Column(name = "access_object_type", nullable = false)
    var accessObjectType: Int = 1,

    @Type(type="pg-uuid")
    @Column(name = "user_id", nullable = false, columnDefinition="uuid")
    var userId: UUID,

    @Column(name = "role", nullable = false)
    var role: RoleType = RoleType.Employee

) : BaseReadEntity() {
    enum class RoleType {
        Employee,
        Owner,
    }
}

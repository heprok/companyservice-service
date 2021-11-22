package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.briolink.servicecompanyservice.common.jpa.enumration.CompanyRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.annotations.Type
import java.util.*
import java.util.UUID.randomUUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "connection_role", schema = "read")
@Entity
class ConnectionRoleReadEntity(
    @Id
    @Type(type="pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Column(name = "name", nullable = false, length = 128)
    var name: String,

    @Column(name = "type", nullable = false)
    private var _type: Int,

) : BaseReadEntity() {
    var type: CompanyRoleTypeEnum
        get() = CompanyRoleTypeEnum.fromInt(_type)
        set(value) {
            _type = value.value
        }
}

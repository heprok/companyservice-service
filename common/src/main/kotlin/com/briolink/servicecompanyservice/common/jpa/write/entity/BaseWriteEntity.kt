package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.UUID
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@TypeDefs(TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
@MappedSuperclass
abstract class BaseWriteEntity {
    @Id
    @Type(type = "pg-uuid")
    @GeneratedValue
    var id: UUID? = null
}

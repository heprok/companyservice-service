package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.UUID
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@TypeDefs(TypeDef(name = "json", typeClass = JsonType::class))
@MappedSuperclass
abstract class BaseWriteEntity {
    @Id
    @Type(type="pg-uuid")
    @GeneratedValue
    var id: UUID? = null
}

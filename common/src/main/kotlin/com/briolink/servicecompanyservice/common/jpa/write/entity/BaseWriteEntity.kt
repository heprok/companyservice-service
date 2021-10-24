package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
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
    @GeneratedValue
    @Type(type = "uuid-char")
    var id: UUID? = null
}

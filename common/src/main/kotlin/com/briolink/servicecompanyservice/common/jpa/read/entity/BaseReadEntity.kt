package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.MappedSuperclass

@TypeDefs(
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
@MappedSuperclass
abstract class BaseReadEntity

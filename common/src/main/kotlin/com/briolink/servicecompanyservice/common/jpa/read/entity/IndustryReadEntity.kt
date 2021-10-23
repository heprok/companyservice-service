package com.briolink.servicecompanyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "industry", catalog = "schema_read")
@Entity
class IndustryReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID
) : BaseReadEntity() {
    @Column(name = "name", nullable = false, length = 128)
    lateinit var name: String
}

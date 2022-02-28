package com.briolink.servicecompanyservice.common.jpa.read.entity

import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company_industry", schema = "read")
@Entity
class CompanyIndustryReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Column(nullable = false)
    var name: String
) : BaseReadEntity()

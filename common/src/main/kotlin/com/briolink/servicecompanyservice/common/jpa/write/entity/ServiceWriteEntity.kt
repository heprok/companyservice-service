package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceEventData
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.net.URL
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Table(name = "service", schema = "write")
@Entity
class ServiceWriteEntity(

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String,

    @Column(name = "price", precision = 10, scale = 2)
    var price: Double? = null,

    @Column(name = "logo", length = 255)
    var logo: URL? = null,

    @Column(name = "description", length = 10240)
    var description: String? = null,

    @Column(name = "deleted")
    var deleted: Instant? = null,

    @Column(name = "hidden")
    var hidden: Boolean = false,

    @Type(type = "pg-uuid")
    @Column(name = "deleted_by")
    var deletedBy: UUID? = null

) : BaseWriteEntity() {
    @CreationTimestamp
    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @UpdateTimestamp
    @Column(name = "changed")
    var changed: Instant? = null

    fun toDomain() = CompanyServiceEventData(
        id = id!!,
        companyId = companyId,
        name = name,
        price = price,
        logo = logo,
        description = description,
        created = created,
        slug = slug,
        hidden = hidden,
    )
}

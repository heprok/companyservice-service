package com.briolink.servicecompanyservice.common.jpa.write.entity

import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyService
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Instant
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.PrePersist
import javax.persistence.Table

@Table(name = "service", catalog = "schema_write")
@Entity
class ServiceWriteEntity(
    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    var companyId: UUID,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String? = null,

    @Column(name = "price")
    var price: Double? = null,

    @Column(name = "logo", length = 255)
    var logo: URL? = null,

    @Column(name = "description", length = 10240)
    var description: String? = null,

    @Column(name = "created")
    var created: Instant? = null

) : BaseWriteEntity() {
    @PrePersist
    fun prePersist() {
        slug = slug ?: StringUtil.slugify("$name $companyId", false)
        created = created ?: Instant.now()
    }

    fun toDomain() = CompanyService(
            id = id!!,
            companyId = companyId,
            name = name,
            price = price,
            logo = logo,
            description = description,
            created = created!!,
            slug = slug!!,
    )

}

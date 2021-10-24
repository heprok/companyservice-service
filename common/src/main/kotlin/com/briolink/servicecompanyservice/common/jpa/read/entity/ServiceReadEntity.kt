package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "service", catalog = "schema_read")
@Entity
class ServiceReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    var id: UUID,

    @Type(type = "uuid-char")
    @Column(name = "company_id", nullable = false, length = 36)
    var companyId: UUID,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String,

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    var data: Data
) : BaseReadEntity() {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("image")
        var image: URL? = null,
        @JsonProperty("verifiedUses")
        var verifiedUses: Int = 0,
        @JsonProperty("price")
        var price: Double? = null,
        @JsonProperty("created")
        var created: LocalDate
    )
}

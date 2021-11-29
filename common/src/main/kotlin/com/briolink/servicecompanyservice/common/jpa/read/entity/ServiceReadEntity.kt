package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "service", schema = "read")
@Entity
class ServiceReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    var id: UUID,

    @Type(type = "pg-uuid")
    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Column(name = "slug", nullable = false, length = 255)
    var slug: String,

) : BaseReadEntity() {

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false)
    lateinit var data: Data

    data class Data(
        @JsonProperty
        var name: String,
        @JsonProperty
        var logo: URL? = null,
        @JsonProperty
        var verifiedUses: Int = 0,
        @JsonProperty
        var price: Double? = null,
        @JsonProperty
        var created: Instant,
        @JsonProperty
        var company: Company,
        @JsonProperty
        var description: String? = null,
        @JsonProperty
        var lastUsed: LocalDate? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Company(
        @JsonProperty
        var id: UUID,
        @JsonProperty
        var name: String,
        @JsonProperty
        var slug: String,
        @JsonProperty
        var logo: URL?,
    )
}

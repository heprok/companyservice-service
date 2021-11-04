package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.time.Instant
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

    ) : BaseReadEntity() {
    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("logo")
        var logo: URL? = null,
        @JsonProperty("verifiedUses")
        var verifiedUses: Int = 0,
        @JsonProperty("price")
        var price: Double? = null,
        @JsonProperty("created")
        var created: Instant,
        @JsonProperty("company")
        var company: Company,
        @JsonProperty("description")
        var description: String? = null,
        @JsonProperty("lastUsed")
        val lastUsed: LocalDate? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Company(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("industry")
        var industry: CompanyReadEntity.Industry?,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL?,
    )
}

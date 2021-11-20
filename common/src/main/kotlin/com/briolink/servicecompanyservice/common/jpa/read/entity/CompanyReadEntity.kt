package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.briolink.servicecompanyservice.common.jpa.dto.location.LocationInfoDto
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.net.URL
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "company", schema = "read")
@Entity
class CompanyReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,
) : BaseReadEntity() {
    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Type(type = "jsonb")
    @Column(name = "data", nullable = false)
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL? = null,
        @JsonProperty("location")
        var location: LocationInfoDto? = null,
        @JsonProperty("industry")
        var industry: Industry? = null,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Industry(
        @JsonProperty("id")
        val id: UUID,
        @JsonProperty("name")
        val name: String,
    )
}

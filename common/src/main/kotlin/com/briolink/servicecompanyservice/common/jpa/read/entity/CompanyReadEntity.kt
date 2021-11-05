package com.briolink.servicecompanyservice.common.jpa.read.entity

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
    @Type(type="pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,
    ) : BaseReadEntity() {

    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL? = null,
        @JsonProperty("location")
        var location: String? = null,
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

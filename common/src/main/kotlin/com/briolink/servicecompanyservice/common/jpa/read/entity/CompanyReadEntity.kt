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

@Table(name = "company", catalog = "schema_read")
@Entity
class CompanyReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID,

) : BaseReadEntity() {
    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL? = null,
    )
}

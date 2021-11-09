package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import java.io.Serializable
import java.net.URL
import java.time.Instant
import java.time.Year
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Table

class ConnectionPK(
) : Serializable {
    lateinit var id: UUID
    lateinit var serviceId: UUID
}

@Table(name = "connection", catalog = "schema_read")
@Entity
@IdClass(ConnectionPK::class)
class ConnectionReadEntity(
    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", nullable = false, length = 36)
    val id: UUID,

    @Id
    @Column(name = "service_id", nullable = false)
    @Type(type = "uuid-char")
    var serviceId: UUID
) : BaseReadEntity() {
    @Column(name = "seller_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    lateinit var sellerId: UUID

    @Column(name = "buyer_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    lateinit var buyerId: UUID

    @Column(name = "buyer_role_id", nullable = false, length = 36)
    @Type(type = "uuid-char")
    lateinit var buyerRoleId: UUID

    @Column(name = "buyer_name", nullable = false, length = 255)
    var buyerName: String? = null

    @Column(name = "buyer_role_name", nullable = false, length = 255)
    var buyerRoleName: String? = null

    @Column(name = "location", length = 255)
    var location: String? = null

    @Type(type = "com.vladmihalcea.hibernate.type.basic.YearType")
    @Column(name = "startCollaboration", nullable = false)
    lateinit var startCollaboration: Year

    @Type(type = "com.vladmihalcea.hibernate.type.basic.YearType")
    @Column(name = "end_collaboration")
    var endCollaboration: Year? = null

    @Column(name = "industry_id", length = 36)
    @Type(type = "uuid-char")
    var industryId: UUID? = null

    @Column(name = "industry_name", length = 255)
    var industryName: String? = null

    @Column(name = "verification_stage", nullable = false)
    lateinit var verificationStage: ConnectionStatus

    @Type(type = "json")
    @Column(name = "buyer_role", columnDefinition = "json")
    var buyerRole: Role? = null

    @Column(name = "created")
    var created: DateTime? = null

    @Type(type = "json")
    @Column(name = "data", nullable = false, columnDefinition = "json")
    lateinit var data: Data

    @PrePersist
    @PreUpdate
    private fun updateInfo() {
        created = created ?: DateTime.now()
        buyerId = data.buyerCompany.id
        buyerRoleName = data.buyerCompany.role.name
        buyerRoleId = data.buyerCompany.role.id
        buyerRole = data.buyerCompany.role
        buyerName = data.buyerCompany.name
        sellerId = data.sellerCompany.id

        industryId = data.industry?.id
        industryName = data.industry?.name

        startCollaboration = data.connectionService.startDate
        endCollaboration = data.connectionService.endDate

    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum class ConnectionStatus(val value: Int) {
        Draft(1),
        Pending(2),
        InProgress(3),
        Verified(4),
        Rejected(5)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Data(
        val connectionId: UUID,
        val serviceId: UUID
    ) {
        @JsonProperty("buyerCompany")
        lateinit var buyerCompany: ParticipantCompany

        @JsonProperty("sellerCompany")
        lateinit var sellerCompany: ParticipantCompany

        @JsonProperty("services")
        lateinit var connectionService: ConnectionService

        @JsonProperty("industry")
        var industry: CompanyReadEntity.Industry? = null
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ParticipantCompany(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("slug")
        var slug: String,
        @JsonProperty("logo")
        var logo: URL?,
        @JsonProperty("verifyUser")
        var verifyUser: VerifyUser,
        @JsonProperty("role")
        var role: Role
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Role(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("role")
        var type: ConnectionRoleReadEntity.RoleType,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ConnectionService(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("name")
        var name: String?,
        @JsonProperty("slug")
        var slug: String? = null,
        @JsonProperty("endDate")
        val endDate: Year?,
        @JsonProperty("startDate")
        val startDate: Year,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VerifyUser(
        @JsonProperty("id")
        var id: UUID,
        @JsonProperty("firstName")
        var firstName: String,
        @JsonProperty("lastName")
        var lastName: String,
        @JsonProperty("image")
        var image: URL?,
        @JsonProperty("slug")
        var slug: String,
    )
}



package com.briolink.servicecompanyservice.common.jpa.read.entity

import com.briolink.servicecompanyservice.common.jpa.converter.YearAttributeConverter
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Type
import java.io.Serializable
import java.net.URL
import java.time.Instant
import java.time.Year
import java.util.*
import javax.persistence.Column
import javax.persistence.Convert
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

@Table(name = "connection", schema = "read")
@Entity
@IdClass(ConnectionPK::class)
class ConnectionReadEntity(
    @Id
    @Type(type="pg-uuid")
    @Column(name = "id", nullable = false)
    val id: UUID,

    @Id
    @Type(type="pg-uuid")
    @Column(name = "service_id", nullable = false)

    var serviceId: UUID
) : BaseReadEntity() {
    @Type(type="pg-uuid")
    @Column(name = "seller_id", nullable = false)
    lateinit var sellerId: UUID

    @Type(type="pg-uuid")
    @Column(name = "buyer_id", nullable = false)
    lateinit var buyerId: UUID

    @Type(type="pg-uuid")
    @Column(name = "buyer_role_id", nullable = false)
    lateinit var buyerRoleId: UUID

    @Column(name = "buyer_name", nullable = false, length = 255)
    var buyerName: String? = null

    @Column(name = "buyer_role_name", nullable = false, length = 255)
    var buyerRoleName: String? = null

    @Column(name = "buyer_role_type", nullable = false, length = 255)
    lateinit var buyerRoleType: ConnectionRoleReadEntity.RoleType

    @Column(name = "location", length = 255)
    var location: String? = null

    @Convert(converter = YearAttributeConverter::class)
    @Column(name = "start_collaboration", columnDefinition="smallint", nullable = false)
    lateinit var startCollaboration: Year

    @Convert(converter = YearAttributeConverter::class)
    @Column(name = "end_collaboration", columnDefinition="smallint")
    var endCollaboration: Year? = null

    @Type(type="pg-uuid")
    @Column(name = "industry_id")
    var industryId: UUID? = null

    @Column(name = "industry_name", length = 255)
    var industryName: String? = null

    @Column(name = "verification_stage", nullable = false)
    lateinit var verificationStage: ConnectionStatus

    @Column(name = "created", nullable = false)
    lateinit var created: Instant

    @Type(type="jsonb")
    @Column(name = "data", nullable = false)
    lateinit var data: Data

    @PrePersist
    @PreUpdate
    private fun updateInfo() {
        buyerId = data.buyerCompany.id
        buyerRoleName = data.buyerCompany.role.name
        buyerRoleId = data.buyerCompany.role.id
        buyerRoleType = data.buyerCompany.role.type
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



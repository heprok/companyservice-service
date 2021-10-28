package com.briolink.servicecompanyservice.api.graphql

import com.briolink.servicecompanyservice.api.types.Company
import com.briolink.servicecompanyservice.api.types.Connection
import com.briolink.servicecompanyservice.api.types.ConnectionRole
import com.briolink.servicecompanyservice.api.types.ConnectionRoleType
import com.briolink.servicecompanyservice.api.types.ConnectionService
import com.briolink.servicecompanyservice.api.types.GraphCompany
import com.briolink.servicecompanyservice.api.types.GraphService
import com.briolink.servicecompanyservice.api.types.GraphicValueCompany
import com.briolink.servicecompanyservice.api.types.GraphicValueService
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.Industry
import com.briolink.servicecompanyservice.api.types.Participant
import com.briolink.servicecompanyservice.api.types.Service
import com.briolink.servicecompanyservice.api.types.User
import com.briolink.servicecompanyservice.api.types.VerificationStage
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.IndustryReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.servicecompanyservice.common.jpa.write.entity.ServiceWriteEntity
import java.net.URL

fun Industry.Companion.fromEntity(entity: IndustryReadEntity) = Industry(
        id = entity.id.toString(),
        name = entity.name,
)

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
        id = entity.id.toString(),
        name = entity.data.name,
        price = entity.data.price,
        company = Company(
                id = entity.data.company.id.toString(),
                name = entity.data.company.name,
                slug = entity.data.company.slug,
                logo = entity.data.company.logo?.let { Image(it) }
        ),
        verifiedUses = entity.data.verifiedUses,
        slug = entity.slug,
        logo = entity.data.logo?.let { Image(it) },
)

fun GraphicValueCompany.Companion.fromCompaniesStats(name: String, companiesStats: StatisticReadEntity.CompaniesStats, limit: Int? = 3) =
        GraphicValueCompany(
                name = name,
                value = companiesStats.totalCount.values.sum(),
                companies = companiesStats.listCompanies.distinctBy { company -> company.name }.let {
                    it.sortedBy { (_, name) -> name }.take(
                            limit ?: it.count(),
                    ).map {
                        GraphCompany.fromEntity(it)
                    }
                },
        )

fun GraphCompany.Companion.fromEntity(entity: StatisticReadEntity.Company) = GraphCompany(
        name = entity.name,
        id = entity.id.toString(),
        slug = entity.slug,
        logo = Image(entity.logo),
        role = ConnectionRole(
                id = entity.role.id.toString(),
                name = entity.role.name,
                type = ConnectionRoleType.values()[entity.role.type.ordinal],
        ),
        industry = entity.industry,
        location = entity.location,
)

fun ConnectionRole.Companion.fromEntity(entity: ConnectionRoleReadEntity) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun GraphicValueService.Companion.fromEntity(entity: StatisticReadEntity.ServiceStats) = GraphicValueService(
        service = GraphService.fromEntity(entity.service),
        value = entity.totalCount,
)

fun GraphService.Companion.fromEntity(entity: StatisticReadEntity.Service) = GraphService(
        name = entity.name,
        slug = entity.slug,
        id = entity.id.toString(),
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
        id = entity.id.toString(),
        buyer = Participant(
                id = entity.data.sellerCompany.id.toString(),
                name = entity.data.sellerCompany.name,
                slug = entity.data.sellerCompany.slug,
                logo = entity.data.sellerCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.sellerCompany.verifyUser.id.toString(),
                        lastName = entity.data.sellerCompany.verifyUser.lastName,
                        firstName = entity.data.sellerCompany.verifyUser.firstName,
                        slug = entity.data.sellerCompany.verifyUser.slug,
                        image = entity.data.sellerCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = entity.data.sellerCompany.role.name,
        ),
        seller = Participant(
                id = entity.data.buyerCompany.id.toString(),
                name = entity.data.buyerCompany.name,
                slug = entity.data.buyerCompany.slug,
                logo = entity.data.buyerCompany.logo?.let {
                    Image(url = it)
                },
                verifyUser = User(
                        id = entity.data.buyerCompany.verifyUser.id.toString(),
                        lastName = entity.data.buyerCompany.verifyUser.lastName,
                        firstName = entity.data.buyerCompany.verifyUser.firstName,
                        slug = entity.data.buyerCompany.verifyUser.slug,
                        image = entity.data.buyerCompany.verifyUser.image?.let {
                            Image(url = it)
                        },
                ),
                role = entity.data.buyerCompany.role.name,
        ),
        services = entity.data.services.map {
            ConnectionService(
                    id = it.id.toString(),
                    name = it.name!!,
                    endDate = it.endDate?.value,
                    startDate = it.startDate.value,
            )
        },
        industry = entity.data.industry.let {
            Industry(id = it.id.toString(), name = it.name)
        },
        verificationStage = when (entity.verificationStage) {
            ConnectionReadEntity.ConnectionStatus.Pending -> VerificationStage.Pending
            ConnectionReadEntity.ConnectionStatus.InProgress -> VerificationStage.Progress
            ConnectionReadEntity.ConnectionStatus.Verified -> VerificationStage.Verified
            else -> VerificationStage.Reject
        },
)

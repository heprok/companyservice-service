package com.briolink.servicecompanyservice.api.graphql

import com.briolink.servicecompanyservice.api.types.Company
import com.briolink.servicecompanyservice.api.types.Connection
import com.briolink.servicecompanyservice.api.types.ConnectionRole
import com.briolink.servicecompanyservice.api.types.ConnectionRoleType
import com.briolink.servicecompanyservice.api.types.ConnectionService
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.Industry
import com.briolink.servicecompanyservice.api.types.Participant
import com.briolink.servicecompanyservice.api.types.Service
import com.briolink.servicecompanyservice.api.types.User
import com.briolink.servicecompanyservice.api.types.VerificationStage
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity

fun Industry.Companion.fromEntity(entity: CompanyReadEntity.Industry) = Industry(
        id = entity.id.toString(),
        name = entity.name,
)

fun Service.Companion.fromEntity(entity: ServiceReadEntity) = Service(
        id = entity.id.toString(),
        name = entity.data.name,
        price = entity.data.price,
        description = entity.data.description,
        company = Company(
                id = entity.data.company.id.toString(),
                name = entity.data.company.name,
                slug = entity.data.company.slug,
                logo = entity.data.company.logo?.let { Image(it) },
        ),
        verifiedUses = entity.data.verifiedUses,
        slug = entity.slug,
        logo = entity.data.logo?.let { Image(it) },
)

fun ConnectionRole.Companion.fromEntity(entity: ConnectionReadEntity.Role) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun ConnectionRole.Companion.fromEntity(entity: ConnectionRoleReadEntity) = ConnectionRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionRoleType.values()[entity.type.ordinal],
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
        id = entity.id.toString(),
        buyer = Participant(
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
                role = ConnectionRole.fromEntity(entity.data.buyerCompany.role),
        ),
        verifySeller = User(
                id = entity.data.sellerCompany.verifyUser.id.toString(),
                lastName = entity.data.sellerCompany.verifyUser.lastName,
                firstName = entity.data.sellerCompany.verifyUser.firstName,
                slug = entity.data.sellerCompany.verifyUser.slug,
                image = entity.data.sellerCompany.verifyUser.image?.let {
                    Image(url = it)
                },
        ),
        service = entity.data.connectionService.let {
            ConnectionService(
                    id = it.id.toString(),
                    name = it.name!!,
                    endDate = it.endDate?.value,
                    startDate = it.startDate.value,
            )
        },
        industry = entity.data.industry?.let { Industry.fromEntity(it) },
        verificationStage = when (entity.verificationStage) {
            ConnectionReadEntity.ConnectionStatus.Pending -> VerificationStage.Pending
            ConnectionReadEntity.ConnectionStatus.InProgress -> VerificationStage.InProgress
            ConnectionReadEntity.ConnectionStatus.Verified -> VerificationStage.Verified
            else -> VerificationStage.Pending
        },
)

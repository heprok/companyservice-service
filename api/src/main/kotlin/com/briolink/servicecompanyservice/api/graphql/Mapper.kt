package com.briolink.servicecompanyservice.api.graphql

import com.briolink.servicecompanyservice.api.types.Company
import com.briolink.servicecompanyservice.api.types.Connection
import com.briolink.servicecompanyservice.api.types.ConnectionCompanyRole
import com.briolink.servicecompanyservice.api.types.ConnectionCompanyRoleType
import com.briolink.servicecompanyservice.api.types.ConnectionParticipant
import com.briolink.servicecompanyservice.api.types.ConnectionService
import com.briolink.servicecompanyservice.api.types.ConnectionStatus
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.Industry
import com.briolink.servicecompanyservice.api.types.Service
import com.briolink.servicecompanyservice.api.types.User
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import java.net.URL

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

fun ConnectionCompanyRole.Companion.fromEntity(entity: ConnectionReadEntity.CompanyRole) = ConnectionCompanyRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionCompanyRoleType.valueOf(entity.type.name),
)

fun ConnectionCompanyRole.Companion.fromEntity(entity: ConnectionRoleReadEntity) = ConnectionCompanyRole(
        id = entity.id.toString(),
        name = entity.name,
        type = ConnectionCompanyRoleType.valueOf(entity.type.name),
)

fun Company.Companion.fromEntity(entity: CompanyReadEntity) = Company(
        id = entity.id.toString(),
        slug = entity.data.slug,
        name = entity.name,
        logo = entity.data.logo?.let { Image(url = entity.data.logo) },
        location = entity.data.location?.location,
)

fun Connection.Companion.fromEntity(entity: ConnectionReadEntity) = Connection(
        id = entity.id.toString(),
        participantFrom = ConnectionParticipant(
                user = User(
                        id = entity.data.participantFrom.user.id.toString(),
                        slug = entity.data.participantFrom.user.slug,
                        image = entity.data.participantFrom.user.image?.let { image -> Image(image) },
                        firstName = entity.data.participantFrom.user.firstName,
                        lastName = entity.data.participantFrom.user.lastName
                ),
                company = Company(
                        id = entity.data.participantFrom.company.id.toString(),
                        slug = entity.data.participantFrom.company.slug,
                        logo = entity.data.participantFrom.company.logo?.let { logo -> Image(logo) },
                        name = entity.data.participantFrom.company.name
                ),
                companyRole = ConnectionCompanyRole(
                        id = entity.participantFromRoleId.toString(),
                        name = entity.participantFromRoleName,
                        type = ConnectionCompanyRoleType.valueOf(entity.participantFromRoleType.name)
                )
        ),
        participantTo = ConnectionParticipant(
                user = User(
                        id = entity.data.participantTo.user.id.toString(),
                        slug = entity.data.participantTo.user.slug,
                        image = entity.data.participantTo.user.image?.let { image -> Image(image) },
                        firstName = entity.data.participantTo.user.firstName,
                        lastName = entity.data.participantTo.user.lastName
                ),
                company = Company(
                        id = entity.data.participantTo.company.id.toString(),
                        slug = entity.data.participantTo.company.slug,
                        logo = entity.data.participantTo.company.logo?.let { logo -> Image(logo) },
                        name = entity.data.participantTo.company.name
                ),
                companyRole = ConnectionCompanyRole(
                        id = entity.participantToRoleId.toString(),
                        name = entity.participantToRoleName,
                        type = ConnectionCompanyRoleType.valueOf(entity.participantToRoleType.name)
                )
        ),
        service = entity.data.service.let {
            ConnectionService(
                    id = it.id.toString(),
                    name = it.serviceName,
                    startDate = it.startDate,
                    endDate = it.endDate
            )
        },
        status = ConnectionStatus.valueOf(entity.status.name),
        industry = entity.data.industry
)

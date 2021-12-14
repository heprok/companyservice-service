package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.servicecompanyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.servicecompanyservice.common.jpa.runAfterTxCommit
import com.briolink.servicecompanyservice.updater.ReloadStatisticByCompanyId
import com.briolink.servicecompanyservice.updater.ReloadStatisticByServiceId
import com.briolink.servicecompanyservice.updater.handler.statistic.StatisticHandlerService
import com.vladmihalcea.hibernate.type.range.Range
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.stream.Collectors

@Transactional
@Service
class ConnectionHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val statisticHandlerService: StatisticHandlerService,
    private val userReadRepository: UserReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun createOrUpdate(connection: Connection, isHidden: Boolean) {
        val participantUsers = userReadRepository.findByIdIsIn(
            listOf(connection.participantFrom.userId, connection.participantTo.userId),
        ).stream().collect(Collectors.toMap(UserReadEntity::id) { v -> v })

        val participantCompanies =
            companyReadRepository.findByIdIsIn(listOf(connection.participantFrom.companyId, connection.participantTo.companyId))
                .stream().collect(Collectors.toMap(CompanyReadEntity::id) { v -> v })

        val buyerCompany =
            (
                if (connection.participantFrom.companyRole.type == ConnectionCompanyRoleType.Buyer)
                    participantCompanies[connection.participantFrom.companyId]
                else participantCompanies[connection.participantTo.companyId]
                )?.data

        connection.services.forEach { connectionService ->
            if (connectionService.serviceId != null) {
                connectionReadRepository.findByConnectionServiceId(connectionService.id)
                    .orElse(
                        ConnectionReadEntity(
                            connectionServiceId = connectionService.id,
                            id = connection.id,
                            serviceId = connectionService.serviceId,
                        ),
                    ).apply {
                        participantFromCompanyId = connection.participantFrom.companyId
                        participantFromUserId = connection.participantFrom.userId
                        participantFromRoleId = connection.participantFrom.companyRole.id
                        participantFromRoleName = connection.participantFrom.companyRole.name
                        participantFromRoleType = CompanyRoleTypeEnum.fromInt(connection.participantFrom.companyRole.type.value)
                        participantToCompanyId = connection.participantTo.companyId
                        participantToUserId = connection.participantTo.userId
                        participantToRoleId = connection.participantTo.companyRole.id
                        participantToRoleName = connection.participantTo.companyRole.name
                        participantToRoleType = CompanyRoleTypeEnum.fromInt(connection.participantTo.companyRole.type.value)
                        dates =
                            if (connectionService.endDate == null) Range.closedInfinite(connectionService.startDate.value)
                            else Range.closed(
                                connectionService.startDate.value,
                                connectionService.endDate.value,
                            )
                        status = ConnectionStatusEnum.valueOf(connection.status.name)
                        created = connection.created
                        this.isHidden = isHidden
                        countryId = buyerCompany?.location?.country?.id
                        stateId = buyerCompany?.location?.state?.id
                        cityId = buyerCompany?.location?.city?.id
                        companyIndustryId = buyerCompany?.industry?.id
                        this.data = ConnectionReadEntity.Data(
                            participantFrom = ConnectionReadEntity.Participant(
                                user = ConnectionReadEntity.User(
                                    id = participantUsers[connection.participantFrom.userId]!!.id,
                                    slug = participantUsers[connection.participantFrom.userId]!!.data.slug,
                                    image = participantUsers[connection.participantFrom.userId]!!.data.image,
                                    firstName = participantUsers[connection.participantFrom.userId]!!.data.firstName,
                                    lastName = participantUsers[connection.participantFrom.userId]!!.data.lastName,
                                ),
                                company = ConnectionReadEntity.Company(
                                    id = connection.participantFrom.companyId,
                                    slug = participantCompanies[connection.participantFrom.companyId]!!.data.slug,
                                    name = participantCompanies[connection.participantFrom.companyId]!!.name,
                                    logo = participantCompanies[connection.participantFrom.companyId]!!.data.logo,
                                ),
                                companyRole = ConnectionReadEntity.CompanyRole(
                                    id = connection.participantFrom.companyRole.id,
                                    name = connection.participantFrom.companyRole.name,
                                    type = CompanyRoleTypeEnum.valueOf(connection.participantFrom.companyRole.type.name),
                                ),
                            ),
                            participantTo = connection.participantTo.let {
                                ConnectionReadEntity.Participant(
                                    user = ConnectionReadEntity.User(
                                        id = participantUsers[connection.participantTo.userId]!!.id,
                                        slug = participantUsers[connection.participantTo.userId]!!.data.slug,
                                        image = participantUsers[connection.participantTo.userId]!!.data.image,
                                        firstName = participantUsers[connection.participantTo.userId]!!.data.firstName,
                                        lastName = participantUsers[connection.participantTo.userId]!!.data.lastName,
                                    ),
                                    company = ConnectionReadEntity.Company(
                                        id = connection.participantTo.companyId,
                                        slug = participantCompanies[connection.participantTo.companyId]!!.data.slug,
                                        name = participantCompanies[connection.participantTo.companyId]!!.name,
                                        logo = participantCompanies[connection.participantTo.companyId]!!.data.logo,
                                    ),
                                    companyRole = it.companyRole.let { role ->
                                        ConnectionReadEntity.CompanyRole(
                                            id = role.id,
                                            name = role.name,
                                            type = CompanyRoleTypeEnum.valueOf(role.type.name),
                                        )
                                    },
                                )
                            },
                            industry = buyerCompany?.industry?.name,
                            location = buyerCompany?.location,
                            service = ConnectionReadEntity.Service(
                                id = connectionService.serviceId,
                                serviceName = connectionService.serviceName,
                                startDate = connectionService.startDate,
                                endDate = connectionService.endDate,
                            ),
                        )
                        connectionReadRepository.save(this).let {
                            if (connection.status == ConnectionStatus.Verified)
                                runAfterTxCommit { statisticHandlerService.refreshByService(ReloadStatisticByServiceId(it.serviceId)) }
                        }
                    }
            }
        }
    }

    fun setHiddenByCompanyId(hidden: Boolean, companyId: UUID, connectionId: UUID) {
        connectionReadRepository.changeVisibilityByConnectionIdAndCompanyId(
            connectionId = connectionId,
            companyId = companyId,
            hidden = hidden,
        )
        runAfterTxCommit { applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(companyId)) }
    }

    fun softDeletedByCompanyId(companyId: UUID, connectionId: UUID) {
        connectionReadRepository.deletedByConnectionIdAndCompanyId(companyId = companyId, connectionId = connectionId)
        runAfterTxCommit { applicationEventPublisher.publishEvent(ReloadStatisticByCompanyId(companyId)) }
    }

    fun delete(connectionId: UUID) {
        connectionReadRepository.deleteByConnectionId(connectionId)
    }

    fun updateUser(user: UserReadEntity) {
        connectionReadRepository.updateUser(
            userId = user.id,
            slug = user.data.slug,
            firstName = user.data.firstName,
            lastName = user.data.lastName,
            image = user.data.image?.toString(),
        )
    }

    fun updateCompany(company: CompanyReadEntity) {
        connectionReadRepository.updateCompany(
            companyId = company.id,
            slug = company.data.slug,
            name = company.name,
            logo = company.data.logo?.toString(),
        )
    }
}

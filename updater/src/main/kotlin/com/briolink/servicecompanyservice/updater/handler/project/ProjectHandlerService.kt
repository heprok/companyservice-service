package com.briolink.servicecompanyservice.updater.handler.project

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
class ProjectHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val statisticHandlerService: StatisticHandlerService,
    private val userReadRepository: UserReadRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun createOrUpdate(project: ProjectEventData, isHidden: Boolean) {
        val participantUsers = userReadRepository.findByIdIsIn(
            listOf(project.participantFrom.userId, project.participantTo.userId),
        ).stream().collect(Collectors.toMap(UserReadEntity::id) { v -> v })

        val participantCompanies =
            companyReadRepository.findByIdIsIn(listOf(project.participantFrom.companyId, project.participantTo.companyId))
                .stream().collect(Collectors.toMap(CompanyReadEntity::id) { v -> v })

        val buyerCompany =
            (
                if (project.participantFrom.companyRole.type == ProjectCompanyRoleType.Buyer)
                    participantCompanies[project.participantFrom.companyId]
                else participantCompanies[project.participantTo.companyId]
                )?.data

        project.services.forEach { connectionService ->
            if (connectionService.serviceId != null) {
                connectionReadRepository.findByConnectionServiceId(connectionService.id)
                    .orElse(
                        ConnectionReadEntity(
                            connectionServiceId = connectionService.id,
                            id = project.id,
                            serviceId = connectionService.serviceId,
                        ),
                    ).apply {
                        participantFromCompanyId = project.participantFrom.companyId
                        participantFromUserId = project.participantFrom.userId
                        participantFromRoleId = project.participantFrom.companyRole.id
                        participantFromRoleName = project.participantFrom.companyRole.name
                        participantFromRoleType = CompanyRoleTypeEnum.fromInt(project.participantFrom.companyRole.type.value)
                        participantToCompanyId = project.participantTo.companyId
                        participantToUserId = project.participantTo.userId
                        participantToRoleId = project.participantTo.companyRole.id
                        participantToRoleName = project.participantTo.companyRole.name
                        participantToRoleType = CompanyRoleTypeEnum.fromInt(project.participantTo.companyRole.type.value)
                        dates =
                            if (connectionService.endDate == null) Range.closedInfinite(connectionService.startDate.value)
                            else Range.closed(
                                connectionService.startDate.value,
                                connectionService.endDate.value,
                            )
                        status = ConnectionStatusEnum.valueOf(project.status.name)
                        created = project.created
                        this.isHidden = isHidden
                        countryId = buyerCompany?.location?.country?.id
                        stateId = buyerCompany?.location?.state?.id
                        cityId = buyerCompany?.location?.city?.id
                        companyIndustryId = buyerCompany?.industry?.id
                        this.data = ConnectionReadEntity.Data(
                            participantFrom = ConnectionReadEntity.Participant(
                                user = ConnectionReadEntity.User(
                                    id = participantUsers[project.participantFrom.userId]!!.id,
                                    slug = participantUsers[project.participantFrom.userId]!!.data.slug,
                                    image = participantUsers[project.participantFrom.userId]!!.data.image,
                                    firstName = participantUsers[project.participantFrom.userId]!!.data.firstName,
                                    lastName = participantUsers[project.participantFrom.userId]!!.data.lastName,
                                ),
                                company = ConnectionReadEntity.Company(
                                    id = project.participantFrom.companyId,
                                    slug = participantCompanies[project.participantFrom.companyId]!!.data.slug,
                                    name = participantCompanies[project.participantFrom.companyId]!!.name,
                                    logo = participantCompanies[project.participantFrom.companyId]!!.data.logo,
                                ),
                                companyRole = ConnectionReadEntity.CompanyRole(
                                    id = project.participantFrom.companyRole.id,
                                    name = project.participantFrom.companyRole.name,
                                    type = CompanyRoleTypeEnum.valueOf(project.participantFrom.companyRole.type.name),
                                ),
                            ),
                            participantTo = project.participantTo.let {
                                ConnectionReadEntity.Participant(
                                    user = ConnectionReadEntity.User(
                                        id = participantUsers[project.participantTo.userId]!!.id,
                                        slug = participantUsers[project.participantTo.userId]!!.data.slug,
                                        image = participantUsers[project.participantTo.userId]!!.data.image,
                                        firstName = participantUsers[project.participantTo.userId]!!.data.firstName,
                                        lastName = participantUsers[project.participantTo.userId]!!.data.lastName,
                                    ),
                                    company = ConnectionReadEntity.Company(
                                        id = project.participantTo.companyId,
                                        slug = participantCompanies[project.participantTo.companyId]!!.data.slug,
                                        name = participantCompanies[project.participantTo.companyId]!!.name,
                                        logo = participantCompanies[project.participantTo.companyId]!!.data.logo,
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
                            if (project.status == ProjectStatus.Verified)
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

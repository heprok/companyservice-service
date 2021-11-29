package com.briolink.servicecompanyservice.api.service

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.PagedList
import com.blazebit.persistence.ParameterHolder
import com.blazebit.persistence.WhereBuilder
import com.briolink.event.publisher.EventPublisher
import com.briolink.servicecompanyservice.api.graphql.SecurityUtil
import com.briolink.servicecompanyservice.api.types.Collaborator
import com.briolink.servicecompanyservice.api.types.ConnectionFilter
import com.briolink.servicecompanyservice.api.types.ConnectionSort
import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic
import com.briolink.servicecompanyservice.common.dto.location.LocationId
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceStatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.enumeration.CompanyRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.servicecompanyservice.common.jpa.enumeration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import org.springframework.stereotype.Service
import java.util.UUID
import javax.persistence.EntityManager

@Service
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceCompanyService: ServiceCompanyService,
    private val entityManager: EntityManager,
    private val eventPublisher: EventPublisher,
    private val criteriaBuilderFactory: CriteriaBuilderFactory
) {
    fun findAll(
        serviceId: UUID,
        companyId: UUID,
        limit: Int,
        offset: Int,
        sort: ConnectionSort,
        filter: ConnectionFilter
    ): PagedList<ConnectionReadEntity> {
        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
        val cb = cbf.from(ConnectionReadEntity::class.java)

        setFilters(serviceId, companyId, cb, filter)

        return cb.orderBy(sort.sortBy.name, sort.direction.name == "ASC").orderByAsc("id").page(offset, limit).resultList
    }

    fun count(companyId: UUID, serviceId: UUID, filter: ConnectionFilter): Long {
        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
        val cb = cbf.from(ConnectionReadEntity::class.java)
        setFilters(serviceId, companyId, cb, filter)
        return cb.countQuery.singleResult
    }

    fun <T> setFilters(
        serviceId: UUID,
        companyId: UUID,
        cb: T,
        filters: ConnectionFilter
    ): T where T : WhereBuilder<T>, T : ParameterHolder<T> {
        cb.whereOr()
            .whereAnd()
            .where("participantToCompanyId").eq(companyId)
            .where("_participantToRoleType").eq(CompanyRoleTypeEnum.Seller.value)
            .endAnd()
            .whereAnd()
            .where("participantFromCompanyId").eq(companyId)
            .where("_participantFromRoleType").eq(CompanyRoleTypeEnum.Seller.value)
            .endAnd()
            .endOr()
        cb.where("serviceId").eq(serviceId)
        cb.where("isDeleted").eq(false)
        cb.where("isHidden").eq(filters.isHidden ?: false)
        with(filters) {
            if (!collaboratorIds.isNullOrEmpty()) {
                cb.whereOr()
                    .whereAnd()
                    .where("participantFromCompanyId").`in`(collaboratorIds.map { UUID.fromString(it) })
                    .where("participantFromCompanyId").notEq(companyId)
                    .endAnd()
                    .whereAnd()
                    .where("participantToCompanyId").`in`(collaboratorIds.map { UUID.fromString(it) })
                    .where("participantToCompanyId").notEq(companyId)
                    .endAnd()
                    .endOr()
            }

            if (datesOfCollaborators?.start != null || datesOfCollaborators?.end != null) {
                cb
                    .whereExpression(
                        "int4range_contains(dates, :collaborationStartDate, :collaborationEndDate) = true",
                    )
                    .setParameter("collaborationStartDate", datesOfCollaborators.start?.value)
                    .setParameter("collaborationEndDate", datesOfCollaborators.end?.value)
            }

            if (!industryIds.isNullOrEmpty()) {
                cb.where("companyIndustryId").`in`(industryIds.map { UUID.fromString(it) })
            }

            if (!locationId.isNullOrEmpty()) {
                val locationId = LocationId.fromString(locationId)
                cb.whereExpression("${locationId.type.name.lowercase()}Id = :locationId").setParameter("locationId", locationId.id)
            }

            if (!status.isNullOrEmpty()) {
                cb.where("_status").`in`(status.map { ConnectionStatusEnum.valueOf(it.name).value })
            }
        }

        return cb
    }

    fun existsConnectionByService(serviceId: UUID): Boolean {
        return connectionReadRepository.existsByServiceId(serviceId = serviceId)
    }

    fun getCollaboratorsUsedForCompany(companyId: UUID, serviceId: UUID, query: String): List<Collaborator> =
        connectionReadRepository.getCollaboratorsUsedForCompany(
            companyId = companyId,
            serviceId = serviceId,
            query = query.trimStart().ifEmpty { null },
        ).map { Collaborator(id = it.id.toString(), name = it.name) }

    fun getIndustriesInConnectionFromCompany(companyId: UUID, serviceId: UUID, query: String): List<CompanyReadEntity.Industry> =
        connectionReadRepository.getIndustriesUsesCompany(
            companyId = companyId,
            serviceId = serviceId,
            query = query.trimStart().ifEmpty { null },
        ).map { CompanyReadEntity.Industry(it.id, it.name) }

    fun hiddenConnectionAndServiceId(serviceId: UUID, connectionId: UUID, isHide: Boolean): Boolean {
        SecurityUtil.currentUserAccountId.let {
            if (serviceCompanyService.getPermission(serviceId = serviceId, userId = it) == UserPermissionRoleTypeEnum.Owner) {
                connectionReadRepository.hiddenByConnectionIdAndServiceId(
                    serviceId = serviceId,
                    connectionId = connectionId,
                    isHide = isHide,
                )
                eventPublisher.publishAsync(CompanyServiceStatisticRefreshEvent(Statistic(serviceId)))
                return true
            }
        }
        return false
    }
}

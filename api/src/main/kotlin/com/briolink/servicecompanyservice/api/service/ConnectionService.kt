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
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceStatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.UserPermissionRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.springframework.stereotype.Service
import java.util.*
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
        limit: Int,
        offset: Int,
        sort: ConnectionSort,
        filter: ConnectionFilter
    ): PagedList<ConnectionReadEntity> {
        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
        val cb = cbf.from(ConnectionReadEntity::class.java)

        setFilters(serviceId, cb, filter)

        return cb.orderBy(sort.sortBy.name, sort.direction.name == "ASC").orderByAsc("id").page(offset, limit).resultList
    }

    fun count(serviceId: UUID, filter: ConnectionFilter): Long {
        val cbf = criteriaBuilderFactory.create(entityManager, ConnectionReadEntity::class.java)
        val cb = cbf.from(ConnectionReadEntity::class.java)
        setFilters(serviceId, cb, filter)
        return cb.countQuery.singleResult
    }

    fun <T> setFilters(
        serviceId: UUID,
        cb: T,
        filters: ConnectionFilter
    ): T where T : WhereBuilder<T>, T : ParameterHolder<T> {
        cb.where("serviceId").eq(serviceId)
        cb.where("isDeleted").eq( false)
        with(filters) {
            cb.where("isHidden").eq(filters.isHidden ?: false)
            if (!collaboratorIds.isNullOrEmpty()) {
                cb.whereOr()
                        .where("participantFromCompanyId").`in`(collaboratorIds.map { UUID.fromString(it) })
                        .where("participantToCompanyId").`in`(collaboratorIds.map { UUID.fromString(it) })
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

            if (!location.isNullOrEmpty()) {
                cb.whereExpression("fts_partial(location, :location) = true").setParameter("location", location)
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

    fun getCollaboratorsUsedForCompany(serviceId: UUID, query: String): List<Collaborator> =
            connectionReadRepository.getCollaboratorsUsedForCompany(
                    serviceId = serviceId,
                    query = query.trimStart().ifEmpty { null },
            ).map {
                Collaborator(id = it.id.toString(), name = it.name)
            }.toList()

    fun getIndustriesInConnectionFromCompany(serviceId: UUID, query: String): List<CompanyReadEntity.Industry> =
            connectionReadRepository.getIndustriesUsesCompany(serviceId = serviceId, query = query.trimStart().ifEmpty { null })
                    .map { CompanyReadEntity.Industry(it.id, it.name) }

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

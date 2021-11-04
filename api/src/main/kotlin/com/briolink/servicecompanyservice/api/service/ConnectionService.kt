package com.briolink.servicecompanyservice.api.service

import com.briolink.servicecompanyservice.api.types.Collaborator
import com.briolink.servicecompanyservice.api.types.ConnectionFilter
import com.briolink.servicecompanyservice.api.types.ConnectionRoleType
import com.briolink.servicecompanyservice.api.types.ConnectionSort
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.betweenDateCollab
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.equalsServiceId
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.fullTextSearchByLocation
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.inBuyerIds
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.inBuyerRoleIds
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.inIndustryIds
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.inVerificationStage
import com.briolink.servicecompanyservice.common.util.PageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager

@Service
class ConnectionService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val entityManager: EntityManager
) {
    fun findAll(
        serviceId: UUID,
        limit: Int,
        offset: Int,
        sort: ConnectionSort,
        filter: ConnectionFilter?
    ): Page<ConnectionReadEntity> {
        val spec = getSpecification(filter).and(equalsServiceId(serviceId))

        val sortBy = Sort.by(Sort.Direction.fromString(sort.direction.name), sort.sortBy.name)
        return connectionReadRepository.findAll(
                spec,
                PageRequest(offset, limit, sortBy),
        )
    }

    fun count(serviceId: UUID, filter: ConnectionFilter?): Long {
        val spec = getSpecification(filter).and(equalsServiceId(serviceId))
        return connectionReadRepository.count(spec)
    }

    fun getSpecification(filter: ConnectionFilter?) = Specification<ConnectionReadEntity> { _, _, _ -> null }
            .and(inIndustryIds(filter?.industryIds?.map { UUID.fromString(it) }))
            .and(inBuyerRoleIds(filter?.collaboratorRoleIds?.map { UUID.fromString(it)}))
            .and(inBuyerIds(filter?.collaboratorIds?.map { UUID.fromString(it)}))
            .and(betweenDateCollab(start = filter?.datesOfCollaborators?.start, end = filter?.datesOfCollaborators?.end))
            .and(inVerificationStage(filter?.verificationStages?.map { ConnectionReadEntity.ConnectionStatus.valueOf(it!!.name) }))
            .and(fullTextSearchByLocation(filter?.location))


    fun existsConnectionByService(serviceId: UUID): Boolean {
        return connectionReadRepository.existsByServiceId(serviceId = serviceId)
    }

    fun getCollaboratorsUsedForCompany(serviceId: UUID, query: String, limit: Int = 10): List<Collaborator> =
            connectionReadRepository.getCollaboratorsUsedForCompany(
                    serviceId = serviceId,
                    query = query,
            ).map {
                Collaborator(id = it.id.toString(), name = it.name)
            }.take(limit).toList()

    fun getConnectionRoleUsedForCompany(serviceId: UUID, query: String, limit: Int = 10): List<ConnectionRoleReadEntity> =
            connectionReadRepository.getCollaboratorsRolesUsedForCompany(
                    serviceId = serviceId,
                    query = query,
            ).map {
                ConnectionRoleReadEntity(it.id, it.name, ConnectionRoleReadEntity.RoleType.Buyer)
            }.take(limit).toList()


    fun getIndustriesInConnectionFromCompany(serviceId: UUID, query: String): List<CompanyReadEntity.Industry> =
            connectionReadRepository.getIndustriesUsesCompany(serviceId = serviceId, query = query)
                    .map { CompanyReadEntity.Industry(it.id, it.name) }
}

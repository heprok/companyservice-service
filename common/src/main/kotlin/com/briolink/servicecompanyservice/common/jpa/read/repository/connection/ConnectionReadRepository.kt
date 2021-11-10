package com.briolink.servicecompanyservice.common.jpa.read.repository.connection

import com.briolink.servicecompanyservice.common.jpa.projection.CollaboratorProjection
import com.briolink.servicecompanyservice.common.jpa.projection.CollaboratorRoleProjection
import com.briolink.servicecompanyservice.common.jpa.projection.IndustryProjection
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID>, JpaSpecificationExecutor<ConnectionReadEntity> {

    fun findBySellerIdOrBuyerId(
        sellerId: UUID,
        buyerId: UUID
    ): List<ConnectionReadEntity>


    fun findByIdAndServiceId(id: UUID, serviceId: UUID): Optional<ConnectionReadEntity>


    @Query(
            value = """
            SELECT 
                distinct c.industryId as id, c.industryName  as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                serviceId = :serviceId AND
                industryName LIKE %:query%
        """,
    )
    fun getIndustriesUsesCompany(
        @Param("query") query: String,
        @Param("serviceId") serviceId: UUID,
    ): List<IndustryProjection>

    @Query(
            value = """
            SELECT 
                distinct c.buyerId as id, c.buyerName as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                serviceId = :serviceId AND
                industryName LIKE %:query%
        """,
    )
    fun getCollaboratorsUsedForCompany(
        @Param("query") query: String,
        @Param("serviceId") serviceId: UUID,
    ): List<CollaboratorProjection>

    @Query(
            value = """
            SELECT 
                distinct c.buyerRoleId as id, c.buyerRoleName as name
            FROM 
                ConnectionReadEntity c
            WHERE 
                serviceId = :serviceId AND
                buyerRoleName LIKE %:query%
        """,
    )
    fun getCollaboratorsRolesUsedForCompany(
        @Param("query") query: String,
        @Param("serviceId") serviceId: UUID,
    ): List<CollaboratorRoleProjection>


    fun existsByServiceId(serviceId: UUID): Boolean
    fun findByServiceId(serviceId: UUID): List<ConnectionReadEntity>


    fun deleteByIdAndServiceId(id: UUID, serviceId: UUID): Long


    override fun deleteById(id: UUID)

    @Modifying
    @Query("DELETE from ConnectionReadEntity c where c.id = ?1")
    fun deleteByConnectionId(id: UUID): Long

}

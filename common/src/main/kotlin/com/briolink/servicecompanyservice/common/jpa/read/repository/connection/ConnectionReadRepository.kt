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

    fun findByIdAndServiceId(id: UUID, serviceId: UUID): Optional<ConnectionReadEntity>

    @Query(
            """
        SELECT cast(company_industry.id as varchar), company_industry.name FROM read.company_industry as company_industry
            WHERE
                company_industry.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    (
                    connection.service_id = :serviceId
                    AND connection.company_industry_id = company_industry.id)
                    LIMIT 1
                ) LIMIT 10
    """,
            nativeQuery = true
    )
    fun getIndustriesUsesCompany(@Param("serviceId") serviceId: String, @Param("query") query: String?): List<IndustryProjection>

    @Query(
            """
        SELECT cast(company.id as varchar), company.name FROM read.company as company
            WHERE
                company.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    connection.service_id = :serviceId
                    LIMIT 1
                ) LIMIT 10
    """,
            nativeQuery = true
    )
    fun getCollaboratorsUsedForCompany(
        @Param("query") query: String,
        @Param("serviceId") serviceId: String,
    ): List<CollaboratorProjection>

    fun existsByServiceId(serviceId: UUID): Boolean
    fun findByServiceId(serviceId: UUID): List<ConnectionReadEntity>

    fun deleteByIdAndServiceId(id: UUID, serviceId: UUID): Long

    override fun deleteById(id: UUID)

    @Modifying
    @Query("DELETE from ConnectionReadEntity c where c.id = ?1")
    fun deleteByConnectionId(id: UUID): Int

}

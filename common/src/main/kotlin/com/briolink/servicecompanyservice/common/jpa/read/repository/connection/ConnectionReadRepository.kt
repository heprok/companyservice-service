package com.briolink.servicecompanyservice.common.jpa.read.repository.connection

import com.briolink.servicecompanyservice.common.jpa.enumeration.ConnectionStatusEnum
import com.briolink.servicecompanyservice.common.jpa.projection.CollaboratorProjection
import com.briolink.servicecompanyservice.common.jpa.projection.IndustryProjection
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface ConnectionReadRepository : JpaRepository<ConnectionReadEntity, UUID> {

    fun findByIdAndServiceId(id: UUID, serviceId: UUID): Optional<ConnectionReadEntity>

    @Query(
        """
        SELECT cast(company_industry.id as varchar), company_industry.name FROM read.company_industry as company_industry
            WHERE
                (:query is null or company_industry.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true) 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    (
                    ((connection.participant_from_role_type = 1 AND connection.participant_from_company_id = :companyId) OR
                    (connection.participant_to_role_type = 1 AND connection.participant_to_company_id = :companyId)) AND
                    connection.service_id = :serviceId AND 
                    connection.company_industry_id = company_industry.id)
                    LIMIT 1
                ) LIMIT 10
    """,
        nativeQuery = true,
    )
    fun getIndustriesUsesCompany(
        @Param("serviceId") serviceId: UUID,
        @Param("companyId") companyId: UUID,
        @Param("query") query: String?
    ): List<IndustryProjection>

    @Query(
        """
        SELECT cast(company.id as varchar), company.name FROM read.company as company
            WHERE
                (:query is null or company.name @@to_tsquery( quote_literal( quote_literal( :query ) ) || ':*' ) = true) 
                AND EXISTS (
                SELECT 1 FROM
                    read.connection as connection
                WHERE
                    ((connection.participant_from_role_type = 1 AND connection.participant_from_company_id = :companyId) OR
                    (connection.participant_to_role_type = 1 AND connection.participant_to_company_id = :companyId)) AND
                    connection.service_id = :serviceId
                    LIMIT 1
                ) LIMIT 10
    """,
        nativeQuery = true,
    )
    fun getCollaboratorsUsedForCompany(
        @Param("serviceId") serviceId: UUID,
        @Param("companyId") companyId: UUID,
        @Param("query") query: String?,
    ): List<CollaboratorProjection>

    fun existsByServiceId(serviceId: UUID): Boolean
    fun findByServiceId(serviceId: UUID): List<ConnectionReadEntity>

    fun deleteByIdAndServiceId(id: UUID, serviceId: UUID): Long

    override fun deleteById(id: UUID)

    @Modifying
    @Query("DELETE from ConnectionReadEntity c where c.id = ?1")
    fun deleteByConnectionId(id: UUID): Int

    @Query(
        """
        select c from ConnectionReadEntity c
        where c.serviceId = ?1 and c._status = ?2 AND c.isDeleted = false AND c.isHidden = false
    """,
    )
    fun getByServiceIdAndStatusAndNotHiddenOrDeleted(
        serviceId: UUID,
        type: Int = ConnectionStatusEnum.Verified.value
    ): List<ConnectionReadEntity>

    @Modifying
    @Query("UPDATE ConnectionReadEntity c SET c.isHidden = ?3 where c.id = ?1 and c.serviceId = ?2")
    fun hiddenByConnectionIdAndServiceId(connectionId: UUID, serviceId: UUID, isHide: Boolean): Int

    @Modifying
    @Query(
        """update ConnectionReadEntity u
           set u.data = case
               when u.participantFromUserId = :userId
                   then function('jsonb_sets', u.data,
                           '{participantFrom,user,slug}', :slug, text,
                           '{participantFrom,user,image}', :image, text,
                           '{participantFrom,user,firstName}', :firstName, text,
                           '{participantFrom,user,lastName}', :lastName, text
                   )
               when u.participantToUserId = :userId
                   then function('jsonb_sets', u.data,
                           '{participantTo,user,slug}', :slug, text,
                           '{participantTo,user,image}', :image, text,
                           '{participantTo,user,firstName}', :firstName, text,
                           '{participantTo,user,lastName}', :lastName, text
                   )
               else data end
           where u.participantFromUserId = :userId or u.participantToUserId = :userId""",
    )
    fun updateUser(
        @Param("userId") userId: UUID,
        @Param("slug") slug: String,
        @Param("firstName") firstName: String,
        @Param("lastName") lastName: String,
        @Param("image") image: String? = null,
    )

    @Modifying
    @Query(
        """update ConnectionReadEntity u
           set u.data = case
               when u.participantFromCompanyId = :companyId
                   then function('jsonb_sets', u.data,
                           '{participantFrom,company,slug}', :slug, text,
                           '{participantFrom,company,image}', :image, text,
                           '{participantFrom,company,name}', :name, text
                   )
               when u.participantToCompanyId = :companyId
                   then function('jsonb_sets', u.data,
                           '{participantTo,company,slug}', :slug, text,
                           '{participantTo,company,image}', :image, text,
                           '{participantTo,company,name}', :name, text
                   )
               else data end
           where
            (u.participantFromCompanyId = :companyId) or
            (u.participantToCompanyId = :companyId)""",
    )
    fun updateCompany(
        @Param("companyId") companyId: UUID,
        @Param("slug") slug: String,
        @Param("name") name: String,
        @Param("image") image: String? = null,
    )
}

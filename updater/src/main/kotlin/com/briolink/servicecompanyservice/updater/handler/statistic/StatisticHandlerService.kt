package com.briolink.servicecompanyservice.updater.handler.statistic

import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.StatisticReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.StatisticReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Year
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class StatisticService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun refreshByService(serviceId: UUID) {
        val statisticByService = StatisticReadEntity(serviceId)
        val serviceUpdated: ServiceReadEntity = serviceReadRepository.findById(serviceId)
                .orElseThrow { throw EntityNotFoundException("$serviceId not found") }

        val statsNumberConnection = StatisticReadEntity.StatsNumberConnection()
        val statsByCountry = StatisticReadEntity.StatsByCountry()
        val statsByIndustry = StatisticReadEntity.StatsByIndustry()
        val statsServiceDuration = StatisticReadEntity.StatsServiceDuration()
//
//        val statsNumberConnection = statisticByCompany.statsNumberConnection ?: StatisticReadEntity.StatsNumberConnection()
//        val statsByCountry = statisticByCompany.statsByCountry ?: StatisticReadEntity.StatsByCountry()
//        val statsByIndustry =  statisticByCompany.statsByIndustry ?: StatisticReadEntity.StatsByIndustry()
//        val statsServiceProvided =  statisticByCompany.statsByCountry ?: StatisticReadEntity.StatsServiceProvided()
        val connectionsByService = connectionReadRepository.findByServiceId(serviceId)
        val yearRange = mutableMapOf<UUID, IntRange>()
        connectionsByService.forEach { connection ->
            val collaborator =
                StatisticReadEntity.Company(
                        id = connection.data.sellerCompany.id,
                        slug = connection.data.sellerCompany.slug,
                        name = connection.data.sellerCompany.name,
                        logo = connection.data.sellerCompany.logo,
                        role = StatisticReadEntity.Role(
                                name = connection.data.sellerCompany.role.name,
                                id = connection.data.sellerCompany.id,
                                type = ConnectionRoleReadEntity.RoleType.valueOf(connection.data.sellerCompany.role.type.name),
                        ),
                )
            .apply {
                val companyRead = companyReadRepository.findById(id).orElseThrow { throw EntityNotFoundException("$id company not found") }
                industry = companyRead.data.industry?.name
                location = companyRead.data.location
            }
            statsServiceDuration.duration[collaborator.id] =  statsServiceDuration.duration.getOrDefault(
                    collaborator.id,
                    IntRange(connection.startCollaboration.value, connection.endCollaboration?.value ?: Year.now().value)
            )
            statsNumberConnection.years[connection.created!!.year] = statsNumberConnection.years.getOrDefault(
                    connection.created!!.year,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

            //TODO add industryString connection, country if null?
            val countyCollaborator: String
            val industryName: String
            companyReadRepository.findById(collaborator.id)
                    .orElseThrow { throw EntityNotFoundException(collaborator.id.toString() + " not found") }.data.also { data ->
                        countyCollaborator = data.location!!.split(",", ignoreCase = true, limit = 3)[1].trimStart()
                        industryName = data.industry!!.name
                    }
            statsByIndustry.industries[industryName] = statsByIndustry.industries.getOrDefault(
                    industryName,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

            statsByCountry.countries[countyCollaborator] = statsByCountry.countries.getOrDefault(
                    countyCollaborator,
                    StatisticReadEntity.CompaniesStats(
                            listCompanies = mutableSetOf(),
                            totalCount = mutableMapOf(),
                    ),
            ).apply {
                this.totalCount[collaborator.id] = this.totalCount.getOrDefault(collaborator.id, 0) + 1
                this.listCompanies.add(collaborator)
            }

        }

        statisticByService.statsNumberConnection = statsNumberConnection
        statisticByService.statsByIndustry = statsByIndustry
        statisticByService.statsByCountry = statsByCountry
        statisticByService.statsServiceDuration = statsServiceDuration
        statisticReadRepository.save(statisticByService)
        serviceReadRepository.save(serviceUpdated)
    }
}


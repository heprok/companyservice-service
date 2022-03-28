package com.briolink.servicecompanyservice.updater.dataloader

import com.briolink.servicecompanyservice.common.dataloader.DataLoader
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.servicecompanyservice.updater.handler.project.ProjectCompanyRole
import com.briolink.servicecompanyservice.updater.handler.project.ProjectCompanyRoleType
import com.briolink.servicecompanyservice.updater.handler.project.ProjectEventData
import com.briolink.servicecompanyservice.updater.handler.project.ProjectHandlerService
import com.briolink.servicecompanyservice.updater.handler.project.ProjectParticipant
import com.briolink.servicecompanyservice.updater.handler.project.ProjectService
import com.briolink.servicecompanyservice.updater.handler.project.ProjectStatus
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import java.time.Year
import java.util.UUID
import kotlin.random.Random

@Component
@Order(2)
class ConnectionDataLoader(
    private var connectionReadRepository: ConnectionReadRepository,
    private var userReadRepository: UserReadRepository,
    private var companyReadRepository: CompanyReadRepository,
    private var serviceReadRepository: ServiceReadRepository,
    private var connectionServiceHandler: ProjectHandlerService,
) : DataLoader() {
    override fun loadData() {
        if (
            connectionReadRepository.count().toInt() == 0 &&
            userReadRepository.count().toInt() != 0 &&
            serviceReadRepository.count().toInt() != 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val listCompany = companyReadRepository.findAll()
            val listUser = userReadRepository.findAll()
            val listConnectionRole = listOf(
                ProjectCompanyRole(UUID.randomUUID(), "Customer", ProjectCompanyRoleType.Seller),
                ProjectCompanyRole(UUID.randomUUID(), "Supplier", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Investor", ProjectCompanyRoleType.Seller),
                ProjectCompanyRole(UUID.randomUUID(), "Investor", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Client", ProjectCompanyRoleType.Buyer),
                ProjectCompanyRole(UUID.randomUUID(), "Vendor", ProjectCompanyRoleType.Seller),
            )
            val projectStatusLists = listOf(ProjectStatus.Verified, ProjectStatus.Pending, ProjectStatus.InProgress)
            for (i in 1..COUNT_CONNECTION) {
                val from = listCompany.random()
                val to = listCompany.random().let {
                    if (it.id == from.id) listCompany.random().let {
                        if (it.id == from.id) listCompany.random() else it
                    } else it
                }
                val listServiceByCompanyFrom = serviceReadRepository.findByCompanyId(from.id)
                val services = mutableListOf<ProjectService>()
                for (j in 0..Random.nextInt(1, 4)) {
                    val startCollab = Year.of(Random.nextInt(2010, 2021))
                    val endCollab = Year.of(Random.nextInt(startCollab.value, 2021))
                    services.add(
                        listServiceByCompanyFrom.random().let {
                            ProjectService(
                                id = it.id,
                                serviceId = it.id,
                                serviceName = it.data.name,
                                startDate = startCollab,
                                endDate = if (Random.nextBoolean()) null else endCollab,
                            )
                        },
                    )
                }
                try {

                    connectionServiceHandler.createOrUpdate(
                        ProjectEventData(
                            id = UUID.randomUUID(),
                            participantFrom = ProjectParticipant(
                                userId = listUser.random().id,
                                userJobPositionTitle = "developer",
                                companyId = from.id,
                                companyRole = listConnectionRole.shuffled()
                                    .find { connectionRoleReadEntity -> connectionRoleReadEntity.type == ProjectCompanyRoleType.Seller }!!, // ktlint-disable max-line-length
                            ),
                            participantTo = ProjectParticipant(
                                userId = listUser.random().id,
                                userJobPositionTitle = "developer",
                                companyId = to.id,
                                companyRole = listConnectionRole.shuffled()
                                    .find { connectionRoleReadEntity -> connectionRoleReadEntity.type == ProjectCompanyRoleType.Buyer }!!, // ktlint-disable max-line-length
                            ),
                            services = ArrayList(services),
                            status = projectStatusLists.random(),
                            created = randomInstant(2010, 2020),
                        ),
                        false,
                    )
                } catch (e: DataIntegrityViolationException) {
                }
            }
        }
    }

    companion object {
        const val COUNT_CONNECTION = 300
    }
}

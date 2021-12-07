package com.briolink.servicecompanyservice.updater.dataloader

import com.briolink.servicecompanyservice.common.dataloader.DataLoader
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.updater.handler.connection.Connection
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionCompanyRole
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionCompanyRoleType
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionHandlerService
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionParticipant
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionService
import com.briolink.servicecompanyservice.updater.handler.connection.ConnectionStatus
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
    private var connectionServiceHandler: ConnectionHandlerService,
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
                ConnectionCompanyRole(UUID.randomUUID(), "Customer", ConnectionCompanyRoleType.Seller),
                ConnectionCompanyRole(UUID.randomUUID(), "Supplier", ConnectionCompanyRoleType.Buyer),
                ConnectionCompanyRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Seller),
                ConnectionCompanyRole(UUID.randomUUID(), "Investor", ConnectionCompanyRoleType.Buyer),
                ConnectionCompanyRole(UUID.randomUUID(), "Client", ConnectionCompanyRoleType.Buyer),
                ConnectionCompanyRole(UUID.randomUUID(), "Vendor", ConnectionCompanyRoleType.Seller),
            )
            val connectionStatusList = listOf(ConnectionStatus.Verified, ConnectionStatus.Pending, ConnectionStatus.InProgress)
            for (i in 1..COUNT_CONNECTION) {
                val from = listCompany.random()
                val to = listCompany.random().let {
                    if (it.id == from.id) listCompany.random().let {
                        if (it.id == from.id) listCompany.random() else it
                    } else it
                }
                val listServiceByCompanyFrom = serviceReadRepository.findByCompanyId(from.id)
                val services = mutableListOf<ConnectionService>()
                for (j in 0..Random.nextInt(1, 4)) {
                    val startCollab = Year.of(Random.nextInt(2010, 2021))
                    val endCollab = Year.of(Random.nextInt(startCollab.value, 2021))
                    services.add(
                        listServiceByCompanyFrom.random().let {
                            ConnectionService(
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
                        Connection(
                            id = UUID.randomUUID(),
                            participantFrom = ConnectionParticipant(
                                userId = listUser.random().id,
                                userJobPositionTitle = "developer",
                                companyId = from.id,
                                companyRole = listConnectionRole.shuffled()
                                    .find { connectionRoleReadEntity -> connectionRoleReadEntity.type == ConnectionCompanyRoleType.Seller }!!, // ktlint-disable max-line-length
                            ),
                            participantTo = ConnectionParticipant(
                                userId = listUser.random().id,
                                userJobPositionTitle = "developer",
                                companyId = to.id,
                                companyRole = listConnectionRole.shuffled()
                                    .find { connectionRoleReadEntity -> connectionRoleReadEntity.type == ConnectionCompanyRoleType.Buyer }!!, // ktlint-disable max-line-length
                            ),
                            services = ArrayList(services),
                            status = connectionStatusList.random(),
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
        const val COUNT_CONNECTION = 0
    }
}

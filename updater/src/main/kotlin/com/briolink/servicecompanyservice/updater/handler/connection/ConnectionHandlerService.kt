package com.briolink.servicecompanyservice.updater.handler.connection

import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionRoleReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityNotFoundException

@Transactional
@Service
class ConnectionHandlerService(
    private val connectionReadRepository: ConnectionReadRepository,
    private val companyReadRepository: CompanyReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val userReadRepository: UserReadRepository,
) {
    fun createOrUpdate(connection: Connection) {
        val sellerRead = companyReadRepository.getById(connection.participantFrom.companyId!!)
        val buyerRead = companyReadRepository.getById(connection.participantTo.companyId!!)
        val userBuyerRead = userReadRepository.getById(connection.participantTo.userId!!)
        val userSellerRead = userReadRepository.getById(connection.participantFrom.userId!!)

        connection.services.forEach { connectionService ->

            if (connectionService.serviceId != null) {
                val connectionRead = connectionReadRepository.findByIdAndServiceId(connection.id, connectionService.serviceId)
                        .orElse(ConnectionReadEntity(connection.id, connectionService.serviceId)).apply {
                            verificationStage = ConnectionReadEntity.ConnectionStatus.valueOf(connection.status.name)
                            created = connection.created
                            data = ConnectionReadEntity.Data(connectionId = connection.id, serviceId = serviceId).apply {
                                buyerCompany = ConnectionReadEntity.ParticipantCompany(
                                        id = buyerRead.id,
                                        name = buyerRead.data.name,
                                        slug = buyerRead.data.slug,
                                        logo = buyerRead.data.logo,
                                        verifyUser = ConnectionReadEntity.VerifyUser(
                                                id = userBuyerRead.id,
                                                firstName = userBuyerRead.data.firstName,
                                                lastName = userBuyerRead.data.lastName,
                                                image = userBuyerRead.data.image,
                                                slug = userBuyerRead.data.slug,
                                        ),
                                        role = ConnectionReadEntity.Role(
                                                id = connection.participantTo.companyRole!!.id,
                                                name = connection.participantTo.companyRole!!.name,
                                                type = ConnectionRoleReadEntity.RoleType.valueOf(connection.participantTo.companyRole!!.type.name),
                                        ),
                                )
                                location = buyerRead.data.location
                                sellerCompany = ConnectionReadEntity.ParticipantCompany(
                                        id = sellerRead.id,
                                        name = sellerRead.data.name,
                                        slug = sellerRead.data.slug,
                                        logo = sellerRead.data.logo,
                                        verifyUser = ConnectionReadEntity.VerifyUser(
                                                id = userSellerRead.id,
                                                firstName = userSellerRead.data.firstName,
                                                lastName = userSellerRead.data.lastName,
                                                image = userSellerRead.data.image,
                                                slug = userSellerRead.data.slug,
                                        ),
                                        role = ConnectionReadEntity.Role(
                                                id = connection.participantFrom.companyRole!!.id,
                                                name = connection.participantFrom.companyRole!!.name,
                                                type = ConnectionRoleReadEntity.RoleType.valueOf(connection.participantFrom.companyRole!!.type.name),
                                        ),
                                )
                                industry = buyerRead.data.industry
                                this.connectionService = serviceReadRepository.findById(connectionService.serviceId)
                                        .orElseThrow { throw EntityNotFoundException(connectionService.serviceId.toString() + " company service not found") }
                                        .let {
                                            ConnectionReadEntity.ConnectionService(
                                                    id = it.id,
                                                    name = it.data.name,
                                                    slug = it.slug,
                                                    startDate = connectionService.startDate!!,
                                                    endDate = connectionService.endDate,
                                            )
                                        }
                            }
                        }

                connectionReadRepository.saveAndFlush(connectionRead)
            }
        }

    }

    fun setStatus(status: ConnectionReadEntity.ConnectionStatus, connectionId: UUID) {
        connectionReadRepository.save(
                connectionReadRepository.findById(connectionId)
                        .orElseThrow { throw EntityNotFoundException("$connectionId connection not found") }.apply {
                            verificationStage = status
                        },
        )
    }

    fun delete(connectionId: UUID) {
        if (connectionReadRepository.existsById(connectionId))
            connectionReadRepository.deleteById(connectionId)
    }

}


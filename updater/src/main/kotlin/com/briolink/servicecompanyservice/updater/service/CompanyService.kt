//package com.briolink.servicecompanyservice.updater.service
//
//import com.briolink.servicecompanyservice.common.jpa.read.entity.UserPermissionRoleReadEntity
//import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
//import com.briolink.servicecompanyservice.common.jpa.read.repository.ConnectionReadRepository
//import com.briolink.servicecompanyservice.common.jpa.read.repository.StatisticReadRepository
//import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import java.util.*
//import javax.persistence.EntityNotFoundException
//
//@Transactional
//@Service
//class CompanyService(
//    private val companyReadRepository: CompanyReadRepository,
//    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
//) {
//    fun setOwner(companyId: UUID, userId: UUID): Boolean {
//        val company =
//                companyReadRepository.findById(companyId).orElseThrow { throw EntityNotFoundException("$companyId company not found") }
//        return if (company.data.createdBy == null) {
//            company.data.createdBy = userId
//            companyReadRepository.save(company)
//            userPermissionRoleReadRepository.save(
//                    UserPermissionRoleReadEntity(
//                            userId = userId,
//                            role = UserPermissionRoleReadEntity.RoleType.Owner,
//                            accessObjectUuid = companyId,
//                    ),
//            )
//            true
//        } else {
//            false
//        }
//    }
//
//    fun setPermission(companyId: UUID, userId: UUID, roleType: UserPermissionRoleReadEntity.RoleType) {
//        userPermissionRoleReadRepository.save(
//                userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
//                        accessObjectUuid = companyId,
//                        userId = userId,
//                )?.apply {
//                    role = roleType
//                } ?: UserPermissionRoleReadEntity(accessObjectUuid = companyId, userId = userId, role = roleType),
//        )
//    }
//
//    fun getPermission(companyId: UUID, userId: UUID): UserPermissionRoleReadEntity.RoleType? {
//        return userPermissionRoleReadRepository.findByAccessObjectUuidAndAccessObjectTypeAndUserId(
//                accessObjectUuid = companyId,
//                accessObjectType = 1,
//                userId = userId,
//        )?.role
//    }
//}
//

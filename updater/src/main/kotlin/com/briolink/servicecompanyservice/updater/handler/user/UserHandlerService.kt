package com.briolink.servicecompanyservice.updater.handler.user

import com.briolink.servicecompanyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserHandlerService(
    private val userReadRepository: UserReadRepository
) {
    fun createOrUpdate(user: UserEventData): UserReadEntity {
        userReadRepository.findById(user.id).orElse(UserReadEntity(user.id)).apply {
            this.data = UserReadEntity.Data(
                firstName = user.firstName,
                lastName = user.lastName,
                image = user.image,
            ).apply {
                this.slug = user.slug
            }
            return userReadRepository.save(this)
        }
    }
}

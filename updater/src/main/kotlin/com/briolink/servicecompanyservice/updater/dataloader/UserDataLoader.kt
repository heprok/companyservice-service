package com.briolink.servicecompanyservice.updater.dataloader

import com.briolink.servicecompanyservice.common.dataloader.DataLoader
import com.briolink.servicecompanyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import com.briolink.servicecompanyservice.updater.handler.userjobposition.UserJobPositionCreatedEvent
import com.briolink.servicecompanyservice.updater.handler.userjobposition.UserJobPositionCreatedEventHandler
import com.briolink.servicecompanyservice.updater.handler.userjobposition.UserJobPositionEventData
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.Random
import java.util.UUID

@Component
@Order(1)
class UserDataLoader(
    var userReadRepository: UserReadRepository,
    private val userJobPositionHandlerService: UserJobPositionCreatedEventHandler,
    private val companyReadRepository: CompanyReadRepository,
) : DataLoader() {
    val listFirstName: List<String> = listOf(
        "Lynch", "Kennedy", "Williams", "Evans", "Jones", "Burton", "Miller", "Smith", "Nelson", "Lucas",
    )

    val listLastName: List<String> = listOf(
        "Scott", "Cynthia", "Thomas", "Thomas", "Lucy", "Dawn", "Jeffrey", "Ann", "Joan", "Lauren",
    )

    override fun loadData() {
        if (userReadRepository.count().toInt() == 0) {
            for (i in 1..COUNT_USER) {
                userReadRepository.save(
                    UserReadEntity(id = UUID.randomUUID()).apply {
                        data = UserReadEntity.Data(
                            firstName = listFirstName.random(),
                            lastName = listLastName.random(),
                            image = if (Random().nextBoolean()) URL("https://placeimg.com/148/148/people") else null,
                        ).apply { slug = StringUtil.slugify(listFirstName.random() + " " + listLastName.random()) }
                    },
                ).also { user ->
                    companyReadRepository.getAllUUID().forEach { companyId ->
                        userJobPositionHandlerService.handle(
                            UserJobPositionCreatedEvent(
                                UserJobPositionEventData(
                                    userId = user.id,
                                    companyId = companyId,
                                    id = UUID.randomUUID(),
                                    title = "Devloper",
                                    isCurrent = false,
                                ),
                            ),
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val COUNT_USER = 10
    }
}

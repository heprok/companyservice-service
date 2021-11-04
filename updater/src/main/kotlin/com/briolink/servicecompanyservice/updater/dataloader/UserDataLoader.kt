package com.briolink.servicecompanyservice.updater.dataloader

import com.briolink.servicecompanyservice.common.jpa.read.entity.UserReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserReadRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*

@Component
@Order(1)
class UserDataLoader(
    var userReadRepository: UserReadRepository,

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
                        UserReadEntity(
                                id = UUID.randomUUID(),
                        ).apply {
                            data = UserReadEntity.Data(
                                    firstName = listFirstName.random(),
                                    lastName = listLastName.random(),
                                    image = URL("https://placeimg.com/148/148/people"),
                            ).apply {
                                slug = StringUtil.slugify(
                                        listFirstName.random() + " " + listLastName.random() ,
                                )
                            }
                        },
                )
            }
        }
    }

    companion object {
        const val COUNT_USER = 10
    }
}

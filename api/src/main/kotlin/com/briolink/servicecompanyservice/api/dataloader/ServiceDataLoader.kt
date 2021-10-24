package com.briolink.servicecompanyservice.api.dataloader

import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.entity.ServiceReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.util.StringUtil
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.util.*
import kotlin.random.Random

@Component
@Order(3)
class ServiceDataLoader(
    var readRepository: ServiceReadRepository,
    var companyReadRepository: CompanyReadRepository,
) : DataLoader() {
    val listName: List<String> = listOf(
            "Advertising on Google services",
            "Software provision",
            "Software provision",
            "Executive Education",
            "Innovation culture",
            "Technology evalution",
            "Executive Education",
            "Innovation culture",
            "Online sales",
            "Product design",
            "Development",
            "Design thinking",
            "Market Assessment",
    )

    override fun loadData() {
        if (readRepository.count().toInt() == 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val companyList = companyReadRepository.findAll()
            for (i in 1..COUNT_SERVICE) {
                readRepository.save(
                        ServiceReadEntity(
                                id = UUID.randomUUID(),
                                companyId = companyList.random().id,
                                slug = StringUtil.slugify(listName[(i % 9)]),
                                data = ServiceReadEntity.Data(
                                        image = URL("https://placeimg.com/640/640/tech"),
                                        created = randomDate(2010, 2021),
                                        name = listName[(i % listName.count())],
//                                        lastUsed = randomDate(2010, 2021),
                                        price = Random.nextDouble(0.0, 6000000.0),
                                        verifiedUses = Random.nextInt(0, 600),
                                        ),
                        ),
                )
            }
        }
    }

    companion object {
        const val COUNT_SERVICE = 2000
    }
}

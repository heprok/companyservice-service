package com.briolink.servicecompanyservice.api.dataloader

import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.write.repository.ServiceWriteRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.net.URL
import java.time.Instant
import kotlin.random.Random

@Component
@Order(3)
class ServiceDataLoader(
    var serviceWriteRepository: ServiceWriteRepository,
    var companyReadRepository: CompanyReadRepository,
    var serviceCompanyService: ServiceCompanyService
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
        if (serviceWriteRepository.count().toInt() == 0 &&
            companyReadRepository.count().toInt() != 0
        ) {
            val companyList = companyReadRepository.findAll()
            for (i in 1..COUNT_SERVICE) {
                serviceCompanyService.create(
                        companyId = companyList.random().id,
                        logo = URL("https://placeimg.com/640/640/tech"),
                        created = Instant.from(randomDate(2010, 2021)),
                        name = listName[(i % listName.count())],
                        price = Random.nextDouble(0.0, 6000000.0),
                        description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",
                        logoTempKey = null,
                )
            }
        }
    }

    companion object {
        const val COUNT_SERVICE = 1000
    }
}

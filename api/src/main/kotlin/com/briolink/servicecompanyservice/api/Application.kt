package com.briolink.servicecompanyservice.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.briolink.servicecompanyservice"])
@EntityScan(
    basePackages = [
        "com.briolink.servicecompanyservice.common.jpa.read.entity",
        "com.briolink.servicecompanyservice.common.jpa.write.entity",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.briolink.servicecompanyservice.common.jpa.read.repository",
        "com.briolink.servicecompanyservice.common.jpa.write.repository",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

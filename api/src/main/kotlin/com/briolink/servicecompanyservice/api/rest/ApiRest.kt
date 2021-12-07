package com.briolink.servicecompanyservice.api.rest

import com.briolink.event.publisher.EventPublisher
import com.briolink.servicecompanyservice.api.dataloader.ServiceDataLoader
import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic
import com.briolink.servicecompanyservice.common.event.v1_0.CompanyServiceStatisticRefreshEvent
import com.briolink.servicecompanyservice.common.jpa.read.repository.UserPermissionRoleReadRepository
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@Transactional
class ApiRest(
    private val eventPublisher: EventPublisher,
    private val serviceDataLoader: ServiceDataLoader,
    private val userPermissionRoleReadRepository: UserPermissionRoleReadRepository,
) {
    @GetMapping("/statistic/refresh")
    fun refreshStatistic(): ResponseEntity<Int> {
        userPermissionRoleReadRepository.deleteAll()
        eventPublisher.publishAsync(
            CompanyServiceStatisticRefreshEvent(Statistic(null)),
        )
        return ResponseEntity.ok(1)
    }

    @GetMapping("/generator/data")
    fun loadData(): ResponseEntity<Int> {
        serviceDataLoader.loadData()
        return ResponseEntity.ok(1)
    }
}

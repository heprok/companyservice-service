package com.briolink.servicecompanyservice.api.rest

import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic
import com.briolink.servicecompanyservice.common.event.v1_0.StatisticRefreshEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class StatisticController(
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @GetMapping("/statistic/refresh")
    fun refreshStatistic(): ResponseEntity<Int> {
        applicationEventPublisher.publishEvent(
                StatisticRefreshEvent(Statistic("refresh")),
        )
        return ResponseEntity.ok(1)
    }
}

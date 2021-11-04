package com.briolink.servicecompanyservice.api.rest

import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic
import com.briolink.servicecompanyservice.common.event.v1_0.StatisticRefreshEvent
import com.briolink.event.publisher.EventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class StatisticController(
    private val eventPublisher: EventPublisher,
) {
    @GetMapping("/statistic/refresh")
    fun refreshStatistic(): ResponseEntity<Int> {
        eventPublisher.publishAsync(
                StatisticRefreshEvent(Statistic("refresh")),
        )
        return ResponseEntity.ok(1)
    }
}

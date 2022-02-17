package com.briolink.servicecompanyservice.api.rest

import com.briolink.lib.sync.model.PeriodDateTime
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/api/v1")
class SyncController(
    private val companyServiceService: ServiceCompanyService,
) {
    @PostMapping("sync")
    @Async
    fun sync(
        @RequestParam startLocalDateTime: String? = null,
        @RequestParam endLocalDateTime: String? = null,
        @NotNull @RequestParam syncId: Int?
    ): ResponseEntity<Any> {
        val periodLocalDateTime = if (startLocalDateTime != null && endLocalDateTime != null) PeriodDateTime(
            startDateTime = LocalDateTime.parse(startLocalDateTime), endDateTime = LocalDateTime.parse(endLocalDateTime),
        ) else null
        companyServiceService.publishSyncEvent(syncId!!, periodLocalDateTime)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

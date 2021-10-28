package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyService
import com.briolink.event.Event

data class CompanyServiceCreatedEvent(override val data: CompanyService) : Event<Domain>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyService) : Event<Domain>("1.0")

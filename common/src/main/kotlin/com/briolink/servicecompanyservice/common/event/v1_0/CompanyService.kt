package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.event.Event
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyService
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceDeletedData
import com.briolink.servicecompanyservice.common.domain.v1_0.Domain

data class CompanyServiceCreatedEvent(override val data: CompanyService) : Event<Domain>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyService) : Event<Domain>("1.0")
data class CompanyServiceDeletedEvent(override val data: CompanyServiceDeletedData) : Event<Domain>("1.0")

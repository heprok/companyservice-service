package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import com.briolink.servicecompanyservice.common.domain.v1_0.ServiceCompany
import com.briolink.event.Event

data class ServiceCompanyCreatedEvent(override val data: ServiceCompany) : Event<Domain>("1.0")
data class ServiceCompanyUpdatedEvent(override val data: ServiceCompany) : Event<Domain>("1.0")

package com.briolink.servicecompanyservice.updater.event

import com.briolink.event.Event
import com.briolink.servicecompanyservice.updater.dto.Company

data class CompanyCreatedEvent(override val data: Company) : Event<Company>("1.0")
data class CompanyUpdatedEvent(override val data: Company) : Event<Company>("1.0")


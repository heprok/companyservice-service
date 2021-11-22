package com.briolink.servicecompanyservice.updater.handler.companyindustry

import com.briolink.event.Event

data class IndustryCreatedEvent(override val data: CompanyIndustry) : Event<CompanyIndustry>("1.0")


package com.briolink.servicecompanyservice.updater.handler.company

import com.briolink.event.Event

data class CompanyEvent(override val data: Company) : Event<Company>("1.0")


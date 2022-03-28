package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.lib.event.Event
import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic

data class CompanyServiceStatisticRefreshEvent(override val data: Statistic) : Event<Domain>("1.0")

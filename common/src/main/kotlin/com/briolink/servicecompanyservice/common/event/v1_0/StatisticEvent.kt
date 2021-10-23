package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.servicecompanyservice.common.domain.v1_0.Domain
import com.briolink.servicecompanyservice.common.domain.v1_0.Statistic
import com.briolink.event.Event

data class StatisticRefreshEvent(override val data: Statistic) : Event<Domain>("1.0")

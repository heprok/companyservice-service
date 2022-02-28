package com.briolink.servicecompanyservice.common.event.v1_0

import com.briolink.event.Event
import com.briolink.lib.sync.SyncData
import com.briolink.lib.sync.SyncEvent
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceDeletedData
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceEventData
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceHideData
import com.briolink.servicecompanyservice.common.domain.v1_0.CompanyServiceStatistic
import com.briolink.servicecompanyservice.common.domain.v1_0.Domain

data class CompanyServiceCreatedEvent(override val data: CompanyServiceEventData) : Event<Domain>("1.0")
data class CompanyServiceUpdatedEvent(override val data: CompanyServiceEventData) : Event<Domain>("1.0")
data class CompanyServiceDeletedEvent(override val data: CompanyServiceDeletedData) : Event<Domain>("1.0")
data class CompanyServiceHideEvent(override val data: CompanyServiceHideData) : Event<Domain>("1.0")
data class CompanyServiceSyncEvent(override val data: SyncData<CompanyServiceEventData>) : SyncEvent<CompanyServiceEventData>("1.0")
data class CompanyServiceStatisticEvent(override val data: CompanyServiceStatistic) : Event<Domain>("1.0")

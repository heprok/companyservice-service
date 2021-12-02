package com.briolink.servicecompanyservice.updater

import java.util.UUID

data class ReloadStatisticByServiceId(val serviceId: UUID)
data class ReloadStatisticByCompanyId(val companyId: UUID)

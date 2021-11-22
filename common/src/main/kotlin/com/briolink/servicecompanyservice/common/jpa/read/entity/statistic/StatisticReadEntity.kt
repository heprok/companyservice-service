package com.briolink.servicecompanyservice.common.jpa.read.entity.statistic

import com.briolink.servicecompanyservice.common.jpa.read.entity.BaseReadEntity
import org.hibernate.annotations.Type
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "statistic", schema = "read")
@Entity
class StatisticReadEntity(
    @Id
    @Type(type = "pg-uuid")
    @Column(name = "service_id", nullable = false)
    val serviceId: UUID,
) : BaseReadEntity() {

    @Type(type = "jsonb")
    @Column(name = "chart_by_country", nullable = false)
    var chartByCountry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry", nullable = false)
    var chartByIndustry: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_number_uses_by_year", nullable = false)
    var chartNumberUsesByYear: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_service_duration", nullable = false)
    var chartByServiceDuration: Chart = Chart()

    @Type(type = "jsonb")
    @Column(name = "chart_by_country_data", nullable = false)
    var chartByCountryData: ChartList<ChartListItemWithNumberOfUses> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_industry_data", nullable = false)
    var chartByIndustryData: ChartList<ChartListItemWithNumberOfUses> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_number_uses_by_year_data", nullable = false)
    var chartNumberUsesByYearData: ChartList<ChartListItemWithNumberOfUses> = ChartList()

    @Type(type = "jsonb")
    @Column(name = "chart_by_service_duration_data", nullable = false)
    var chartByServiceDurationData: ChartList<ChartListItemWithDuration> = ChartList()
}

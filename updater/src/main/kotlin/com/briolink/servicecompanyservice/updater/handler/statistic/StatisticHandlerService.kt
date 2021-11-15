package com.briolink.servicecompanyservice.updater.handler.statistic

import com.briolink.servicecompanyservice.common.jpa.enumration.CompanyRoleTypeEnum
import com.briolink.servicecompanyservice.common.jpa.enumration.ConnectionStatusEnum
import com.briolink.servicecompanyservice.common.jpa.read.entity.ConnectionReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.Chart
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartDataList
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartItem
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartList
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartListItemWithDuration
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartListItemWithNumberOfUses
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartTabItem
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.ServiceReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.connection.ConnectionReadRepository
import com.briolink.servicecompanyservice.common.jpa.read.repository.StatisticReadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Year
import java.time.ZoneId
import java.util.*

@Transactional
@Service
class StatisticHandlerService(
    private val statisticReadRepository: StatisticReadRepository,
    private val connectionReadRepository: ConnectionReadRepository,
    private val serviceReadRepository: ServiceReadRepository,
    private val companyReadRepository: CompanyReadRepository,
) {
    fun refreshByService(serviceId: UUID) {
        deleteByServiceId(serviceId)
        connectionReadRepository.getByServiceIdAndStatus(serviceId, ConnectionStatusEnum.Verified.value).forEach {
            addConnectionToStats(it)
        }
    }

    fun addConnectionToStats(connectionReadEntity: ConnectionReadEntity) {
        if (connectionReadEntity.status == ConnectionStatusEnum.Verified) {

            val serviceStatistic = statisticReadRepository.findByServiceId(connectionReadEntity.serviceId) ?: StatisticReadEntity(
                    connectionReadEntity.serviceId,
            )

            val collaboratorParticipant = if (connectionReadEntity.participantFromRoleType == CompanyRoleTypeEnum.Buyer)
                connectionReadEntity.data.participantFrom else connectionReadEntity.data.participantTo

            val industry = connectionReadEntity.data.industry
            // chart data by country
            val country = connectionReadEntity.location?.let {
                it.split(",", ignoreCase = true, limit = 3)[0].trimStart()
            }
            if (!country.isNullOrBlank()) {
                serviceStatistic.chartByCountryData.data.getOrPut(country) { ChartDataList(country, mutableListOf()) }.also { list ->
                    when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                        -1 -> list.items.add(
                                ChartListItemWithNumberOfUses(
                                        companyId = collaboratorParticipant.company.id,
                                        industry = industry,
                                        companyRoles = mutableSetOf(collaboratorParticipant.companyRole.name),
                                        numberOfUses = 1,
                                ),
                        )
                        else -> {
                            list.items[i].numberOfUses = list.items[i].numberOfUses + 1
                            list.items[i].companyRoles.add(collaboratorParticipant.companyRole.name)
                        }
                    }
                }
            } else {
                serviceStatistic.chartByCountryData = ChartList()
            }

            serviceStatistic.chartByCountry = getChart(serviceStatistic.chartByCountryData)

            // chart data by industry
            if (!industry.isNullOrBlank()) {
                serviceStatistic.chartByIndustryData.data.getOrPut(industry) { ChartDataList(industry, mutableListOf()) }.also { list ->
                    when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                        -1 -> list.items.add(
                                ChartListItemWithNumberOfUses(
                                        companyId = collaboratorParticipant.company.id,
                                        industry = industry,
                                        companyRoles = mutableSetOf(collaboratorParticipant.companyRole.name),
                                        numberOfUses = 1,
                                ),
                        )
                        else -> {
                            list.items[i].numberOfUses = list.items[i].numberOfUses.inc()
                            list.items[i].companyRoles.add(collaboratorParticipant.companyRole.name)
                        }
                    }
                }
            } else {
                serviceStatistic.chartByIndustryData = ChartList()
            }
            serviceStatistic.chartByIndustry = getChart(serviceStatistic.chartByIndustryData)

            // chart data by services provided
            serviceStatistic.chartByServiceDurationData.data.getOrPut(collaboratorParticipant.company.id.toString()) {
                ChartDataList(collaboratorParticipant.company.id.toString(), mutableListOf())
            }.also { list ->
                when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                    -1 -> list.items.add(
                            ChartListItemWithDuration(
                                    companyId = collaboratorParticipant.company.id,
                                    duration = connectionReadEntity.data.service.let {
                                        if (it.endDate == null) Year.now().value - it.startDate.value else it.endDate!!.value - it.startDate.value
                                    },
                            ),
                    )
                    else -> list.items[i].duration = list.items[i].duration.let {
                        val startDateYear = connectionReadEntity.data.service.startDate.value
                        val endDateYear = connectionReadEntity.data.service.endDate?.value
                        val duration = (endDateYear ?: Year.now().value) - startDateYear
                        if (it <= duration) duration else it
                    }
                }
            }
            serviceStatistic.chartByServiceDuration = getChartDuration(serviceStatistic.chartByServiceDurationData)

            // chart data by industry
            val createdYear = connectionReadEntity.created.atZone(ZoneId.systemDefault()).year.toString()
            serviceStatistic.chartNumberUsesByYearData.data.getOrPut(createdYear) {
                ChartDataList(createdYear, mutableListOf())
            }.also { list ->
                when (val i = list.items.indexOfFirst { it.companyId == collaboratorParticipant.company.id }) {
                    -1 -> list.items.add(
                            ChartListItemWithNumberOfUses(
                                    companyId = collaboratorParticipant.company.id,
                                    industry = industry,
                                    companyRoles = mutableSetOf(collaboratorParticipant.companyRole.name),
                                    numberOfUses = 1,
                            ),
                    )
                    else -> {
                        list.items[i].numberOfUses = list.items[i].numberOfUses.inc()
                        list.items[i].companyRoles.add(collaboratorParticipant.companyRole.name)
                    }
                }
            }
            serviceStatistic.chartNumberUsesByYear = getChart(
                    serviceStatistic.chartNumberUsesByYearData,
                    true,
            ) { it.sortedBy { el -> el.first.toInt() } }

            statisticReadRepository.save(serviceStatistic)
        }

    }

    private fun deleteByServiceId(serviceId: UUID) {
        statisticReadRepository.deleteByServiceId(serviceId)
    }

    private fun getChartDuration(
        chartList: ChartList<ChartListItemWithDuration>,
    ): Chart {
        val tabs = mutableListOf<ChartTabItem>()
        val items = mutableListOf<ChartItem>()

        val sortedChartListData = chartList.data.toList().let {
            it.sortedByDescending { (_, value) -> value.items[0].duration }
        }

        sortedChartListData.forEachIndexed { index, pair ->
            if (index < COUNT_MAX_VIEW_CHART) {
                val companyIds = pair.second.items
                        .toSortedSet(compareBy { v -> v.companyId })
                        .take(COUNT_COMPANY_ON_HINT).map { it.companyId }.toMutableSet()
                items.add(
                        ChartItem(
                                key = pair.first,
                                name = pair.second.name,
                                value = pair.second.items[0].duration,
                                companyIds = companyIds,
                        ),
                )
            }
        }

        return Chart(items = items, tabs = tabs)
    }

    fun getChart(
        chartList: ChartList<ChartListItemWithNumberOfUses>,
        withoutOther: Boolean = false,
        sortSelector: ((List<Pair<String, ChartDataList<ChartListItemWithNumberOfUses>>>) -> List<Pair<String, ChartDataList<ChartListItemWithNumberOfUses>>>)? = null
    ): Chart {
        val tabs = mutableListOf<ChartTabItem>()
        val items = mutableListOf<ChartItem>()

        val sortedChartListData =
                if (sortSelector != null)
                    sortSelector(chartList.data.toList())
                else
                    chartList.data.toList().sortedByDescending { it.second.items.sumOf { it.numberOfUses } }
        var otherIndex = -1

        sortedChartListData.forEachIndexed { index, pair ->
            tabs.add(
                    ChartTabItem(
                            id = pair.first,
                            name = pair.second.name,
                            total = pair.second.items.sumOf { it.numberOfUses },
                    ),
            )

            if (index < COUNT_MAX_VIEW_CHART || withoutOther) {
                val companyIds = pair.second.items
                        .toSortedSet(compareBy { v -> v.companyId })
                        .take(COUNT_COMPANY_ON_HINT).map { it.companyId }.toMutableSet()
                items.add(
                        ChartItem(
                                key = pair.first,
                                name = pair.second.name,
                                value = pair.second.items.sumOf { it.numberOfUses },
                                companyIds = companyIds,
                        ),
                )
            } else {
                if (otherIndex == -1) {
                    items.add(ChartItem(key = OTHER_KEY, name = OTHER_NAME, companyIds = mutableSetOf(), value = 0))
                    otherIndex = items.lastIndex
                }

                items[otherIndex] = items[otherIndex].apply {
                    if (companyIds.size < COUNT_COMPANY_ON_HINT) {
                        companyIds.addAll(
                                pair.second.items.toSortedSet(compareBy { v -> v.companyId }).take(COUNT_COMPANY_ON_HINT)
                                        .map { it.companyId },
                        )
                        companyIds = companyIds.take(COUNT_COMPANY_ON_HINT).toMutableSet()
                    }
                    this.value = this.value.plus(pair.second.items.sumOf { it.numberOfUses })
                }
            }
        }

        return Chart(items = items, tabs = tabs)
    }

    companion object {
        const val COUNT_MAX_VIEW_CHART = 5
        const val COUNT_COMPANY_ON_HINT = 3
        const val OTHER_KEY = "-1"
        const val OTHER_NAME = "Other"
    }
}


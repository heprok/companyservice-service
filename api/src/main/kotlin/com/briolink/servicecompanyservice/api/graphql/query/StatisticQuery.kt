package com.briolink.servicecompanyservice.api.graphql.query

import com.blazebit.persistence.CriteriaBuilderFactory
import com.briolink.servicecompanyservice.api.graphql.fromEntity
import com.briolink.servicecompanyservice.api.types.ChartByCountry
import com.briolink.servicecompanyservice.api.types.ChartByIndustry
import com.briolink.servicecompanyservice.api.types.ChartByNumberUsesItem
import com.briolink.servicecompanyservice.api.types.ChartByServiceDuration
import com.briolink.servicecompanyservice.api.types.ChartByServiceDurationItem
import com.briolink.servicecompanyservice.api.types.ChartItemHint
import com.briolink.servicecompanyservice.api.types.ChartItemWithHint
import com.briolink.servicecompanyservice.api.types.ChartNumberUsesConnectionByYear
import com.briolink.servicecompanyservice.api.types.ChartTabItem
import com.briolink.servicecompanyservice.api.types.Company
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.ServiceCharts
import com.briolink.servicecompanyservice.common.jpa.read.entity.CompanyReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.Chart
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartListItem
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartListItemWithDuration
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.ChartListItemWithNumberOfUses
import com.briolink.servicecompanyservice.common.jpa.read.entity.statistic.StatisticReadEntity
import com.briolink.servicecompanyservice.common.jpa.read.repository.CompanyReadRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.vladmihalcea.hibernate.type.util.ObjectMapperWrapper
import graphql.schema.DataFetchingEnvironment
import org.springframework.security.access.prepost.PreAuthorize
import java.util.Objects
import java.util.UUID
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.EntityManager
import javax.persistence.Tuple

inline fun <reified T> Tuple.getOrNull(alias: String): T? =
    try {
        this.get(alias, T::class.java)
    } catch (e: IllegalArgumentException) {
        null
    }

@DgsComponent
class StatisticQuery(
    private val entityManager: EntityManager,
    private val criteriaBuilderFactory: CriteriaBuilderFactory,
    private val companyReadRepository: CompanyReadRepository
) {
    fun collectCompanyIds(vararg values: Any?): MutableSet<UUID> {
        val companyIds = mutableSetOf<UUID>()
        values.forEach { v ->
            when (v) {
                is Chart -> {
                    companyIds.addAll(v.items.map { it.companyIds }.flatten())
                }
                is List<*> -> {
                    companyIds.addAll(v.mapNotNull { if (it is ChartListItem) it.companyId else null })
                }
            }
        }
        return companyIds
    }

    fun <T> mapRawData(raw: String?, type: TypeReference<T>) =
        if (raw != null) ObjectMapperWrapper.INSTANCE.objectMapper.readValue(raw, type) else null

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    fun getCharts(@InputArgument serviceId: String, dfe: DataFetchingEnvironment): ServiceCharts {
        val cbf = criteriaBuilderFactory.create(entityManager, Tuple::class.java)
        val cb = cbf.from(StatisticReadEntity::class.java)

        cb.where("serviceId").eq(UUID.fromString(serviceId))

        if (dfe.selectionSet.containsAnyOf("numberUsesByYear/data", "numberUsesByYear/tabs"))
            cb.select("chartNumberUsesByYear", "chartNumberUsesByYear")
        if (dfe.selectionSet.containsAnyOf("byCountry/data", "byCountry/tabs"))
            cb.select("chartByCountry", "chartByCountry")
        if (dfe.selectionSet.containsAnyOf("byIndustry/data", "byIndustry/tabs"))
            cb.select("chartByIndustry", "chartByIndustry")
        if (dfe.selectionSet.containsAnyOf("byServiceDuration/data"))
            cb.select("chartByServiceDuration", "chartByServiceDuration")

        if (dfe.selectionSet.contains("numberUsesByYear/listByTab"))
            cb
                .select("jsonb_get(chartNumberUsesByYearData, 'data', :dk1, 'items')", "chartNumberUsesByYearData")
                .setParameter("dk1", dfe.selectionSet.getFields("numberUsesByYear/listByTab")[0].arguments["id"])

        if (dfe.selectionSet.contains("byCountry/listByTab"))
            cb
                .select("jsonb_get(chartByCountryData, 'data', :dk2, 'items')", "chartByCountryData")
                .setParameter("dk2", dfe.selectionSet.getFields("byCountry/listByTab")[0].arguments["id"])

        if (dfe.selectionSet.contains("byIndustry/listByTab"))
            cb
                .select("jsonb_get(chartByIndustryData, 'data', :dk3, 'items')", "chartByIndustryData")
                .setParameter("dk3", dfe.selectionSet.getFields("byIndustry/listByTab")[0].arguments["id"])

        val result = cb.resultList.firstOrNull()

        val chartNumberUsesByYear = result?.getOrNull<Chart>("chartNumberUsesByYear")
        val chartByCountry = result?.getOrNull<Chart>("chartByCountry")
        val chartByIndustry = result?.getOrNull<Chart>("chartByIndustry")
        val chartByServiceDuration = result?.getOrNull<Chart>("chartByServiceDuration")
        val chartNumberUsesByYearData =
            mapRawData(
                result?.getOrNull("chartNumberUsesByYearData"),
                object : TypeReference<List<ChartListItemWithNumberOfUses>>() {},
            )
        val chartByCountryData =
            mapRawData(result?.getOrNull("chartByCountryData"), object : TypeReference<List<ChartListItemWithNumberOfUses>>() {})
        val chartByIndustryData =
            mapRawData(result?.getOrNull("chartByIndustryData"), object : TypeReference<List<ChartListItemWithNumberOfUses>>() {})
        val chartByServiceDurationData =
            mapRawData(result?.getOrNull("chartByServiceDurationData"), object : TypeReference<List<ChartListItemWithDuration>>() {})

        val companyIds = collectCompanyIds(
            chartNumberUsesByYear,
            chartByCountry,
            chartByIndustry,
            chartByServiceDuration,
            chartNumberUsesByYearData,
            chartByCountryData,
            chartByIndustryData,
            chartByServiceDurationData,
        )

        val companies: Map<UUID, CompanyReadEntity> =
            companyReadRepository
                .findByIdIsIn(companyIds.toList())
                .parallelStream()
                .collect(Collectors.toMap(CompanyReadEntity::id, Function.identity()))

        val mapTabs = { chart: Chart? ->
            chart?.tabs?.map {
                ChartTabItem(
                    id = it.id,
                    name = it.name,
                    total = it.total,
                )
            }.orEmpty()
        }

        val mapData = { chart: Chart? ->
            chart?.items?.map {
                ChartItemWithHint(
                    it.key,
                    it.name,
                    it.value,
                    hints = it.companyIds
                        .stream().map(companies::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()).map { hint ->
                            ChartItemHint(
                                name = hint!!.name,
                                image = hint.data.logo?.let { logo -> Image(logo) },
                            )
                        },
                )
            }.orEmpty()
        }

        return ServiceCharts(
            ChartNumberUsesConnectionByYear(
                data = mapData(chartNumberUsesByYear),
                tabs = mapTabs(chartNumberUsesByYear),
                listByTab = chartNumberUsesByYearData?.map {
                    ChartByNumberUsesItem(
                        company = Company.fromEntity(companies[it.companyId]!!),
                        companyRoles = it.companyRoles.toList(),
                        industry = it.industry,
                        numberOfUses = it.numberOfUses,
                    )
                }.orEmpty(),
            ),
            ChartByCountry(
                data = mapData(chartByCountry),
                tabs = mapTabs(chartByCountry),
                listByTab = chartByCountryData?.map {
                    ChartByNumberUsesItem(
                        company = Company.fromEntity(companies[it.companyId]!!),
                        companyRoles = it.companyRoles.toList(),
                        industry = it.industry,
                        numberOfUses = it.numberOfUses,
                    )
                }.orEmpty(),
            ),
            ChartByIndustry(
                data = mapData(chartByIndustry),
                tabs = mapTabs(chartByIndustry),
                listByTab = chartByIndustryData?.map {
                    ChartByNumberUsesItem(
                        company = Company.fromEntity(companies[it.companyId]!!),
                        companyRoles = it.companyRoles.toList(),
                        industry = it.industry,
                        numberOfUses = it.numberOfUses,
                    )
                }.orEmpty(),
            ),
            ChartByServiceDuration(
                data = chartByServiceDuration?.items?.map {
                    ChartByServiceDurationItem(
                        company = Company.fromEntity(companies[it.companyIds.first()]!!),
                        duration = it.value,
                    )
                }.orEmpty(),
            ),
        )
    }
}

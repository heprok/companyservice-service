package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.event.publisher.EventPublisher
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.BaseResult
import com.briolink.servicecompanyservice.api.types.CreateServiceInput
import com.briolink.servicecompanyservice.api.types.CreateServiceResult
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.ServiceResultData
import com.briolink.servicecompanyservice.api.types.UpdateServiceInput
import com.briolink.servicecompanyservice.api.types.UpdateServiceResult
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@DgsComponent
class ServiceMutation(
    private val serviceCompanyService: ServiceCompanyService,
    private val eventPublisher: EventPublisher
) {
    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun uploadServiceImage(
        @InputArgument("id") id: String,
        @InputArgument("image") image: MultipartFile?
    ): Image? {
        return Image(serviceCompanyService.uploadProfileImage(UUID.fromString(id), image))
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun createService(
        @InputArgument("companyId") companyId: String,
        @InputArgument("input") input: CreateServiceInput
    ): CreateServiceResult {
        val entity = serviceCompanyService.create(
            companyId = UUID.fromString(companyId),
            price = input.price,
            name = input.name,
            description = input.description,
            fileImage = input.logo,
        )

        return CreateServiceResult(
            userErrors = listOf(),
            data = ServiceResultData(id = entity.id.toString(), slug = entity.slug),
        )
    }

    @DgsMutation
    @PreAuthorize("@servletUtil.isIntranet()")
    fun createServiceLocal(
        @InputArgument("companyId") companyId: String,
        @InputArgument("name") name: String
    ): CreateServiceResult {
        return (
            serviceCompanyService.findByNameAndCompanyId(companyId = UUID.fromString(companyId), name = name)
                ?: serviceCompanyService.create(companyId = UUID.fromString(companyId), name = name)
            ).let { CreateServiceResult(data = ServiceResultData(id = it.id.toString(), slug = it.slug), userErrors = listOf()) }
    }

    @DgsMutation
    @PreAuthorize("@servletUtil.isIntranet()")
    fun deleteServiceLocal(
        @InputArgument("serviceId") serviceId: String,
        @InputArgument("userId") userId: String
    ): BaseResult {
        serviceCompanyService.delete(UUID.fromString(serviceId), deletedBy = UUID.fromString(userId))
        return BaseResult(success = true)
    }

    @DgsMutation
    @PreAuthorize("@servletUtil.isIntranet()")
    fun hideServiceLocal(
        @InputArgument("serviceId") serviceId: String
    ): BaseResult {
        serviceCompanyService.hide(UUID.fromString(serviceId))
        return BaseResult(success = true)
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updateService(
        @InputArgument("id") id: String,
        @InputArgument("input") input: UpdateServiceInput
    ): UpdateServiceResult {
        serviceCompanyService.update(
            id = UUID.fromString(id),
            price = input.price,
            description = input.description,
        ) ?: throw DgsEntityNotFoundException()

        return UpdateServiceResult(
            success = true,
            userErrors = listOf(),
        )
    }
}

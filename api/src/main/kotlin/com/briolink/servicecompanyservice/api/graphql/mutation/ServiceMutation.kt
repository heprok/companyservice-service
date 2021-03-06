package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.lib.permission.model.PermissionRight
import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.BaseResult
import com.briolink.servicecompanyservice.api.types.CreateServiceInput
import com.briolink.servicecompanyservice.api.types.CreateServiceResult
import com.briolink.servicecompanyservice.api.types.Error
import com.briolink.servicecompanyservice.api.types.Image
import com.briolink.servicecompanyservice.api.types.ServiceResultData
import com.briolink.servicecompanyservice.api.types.UpdateServiceInput
import com.briolink.servicecompanyservice.api.types.UpdateServiceResult
import com.briolink.servicecompanyservice.api.util.SecurityUtil
import com.briolink.servicecompanyservice.api.util.StringUtil
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
) {
    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun uploadServiceImage(
        @InputArgument("id") id: String,
        @InputArgument("image") image: MultipartFile?
    ): Image? {
        val companyId = serviceCompanyService.getCompanyIdByServiceId(UUID.fromString(id)) ?: throw DgsEntityNotFoundException()
        if (!serviceCompanyService.isHavePermission(
                companyId = companyId,
                userId = SecurityUtil.currentUserAccountId,
                right = PermissionRight("Company", "EditCompanyService"),
                serviceId = UUID.fromString(id),
            )
        ) return null

        return Image(serviceCompanyService.uploadProfileImage(UUID.fromString(id), image))
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun createService(
        @InputArgument("companyId") companyId: String,
        @InputArgument("input") input: CreateServiceInput
    ): CreateServiceResult {
        if (!serviceCompanyService.isHavePermission(
                companyId = UUID.fromString(companyId),
                userId = SecurityUtil.currentUserAccountId,
                right = PermissionRight("Company", "EditCompanyService"),
            )
        ) return CreateServiceResult(userErrors = listOf(Error(code = "403 Permission denied")))

        val entity = serviceCompanyService.create(
            companyId = UUID.fromString(companyId),
            price = input.price,
            name = StringUtil.trimAllSpaces(input.name),
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
            serviceCompanyService.findByNameAndCompanyId(companyId = UUID.fromString(companyId), name = StringUtil.trimAllSpaces(name))
                ?: serviceCompanyService.create(companyId = UUID.fromString(companyId), name = StringUtil.trimAllSpaces(name))
            ).let { serviceWriteEntity ->
            CreateServiceResult(
                data = ServiceResultData(
                    id = serviceWriteEntity.id.toString(),
                    slug = serviceWriteEntity.slug,
                    logo = serviceWriteEntity.logo?.let { Image(it) },
                    description = serviceWriteEntity.description,
                    price = serviceWriteEntity.price,

                ),
                userErrors = listOf(),
            )
        }
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
        @InputArgument serviceId: String,
        @InputArgument hidden: Boolean = true
    ): BaseResult {
        serviceCompanyService.toggleVisibility(UUID.fromString(serviceId), hidden)
        return BaseResult(success = true)
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    fun updateService(
        @InputArgument("id") id: String,
        @InputArgument("input") input: UpdateServiceInput
    ): UpdateServiceResult {
        val companyId = serviceCompanyService.getCompanyIdByServiceId(UUID.fromString(id)) ?: throw DgsEntityNotFoundException()

        if (!serviceCompanyService.isHavePermission(
                companyId = companyId,
                userId = SecurityUtil.currentUserAccountId,
                right = PermissionRight("Company", "EditCompanyService"),
                serviceId = UUID.fromString(id),
            )
        ) return UpdateServiceResult(userErrors = listOf(Error(code = "403 Permission denied")))

        serviceCompanyService.update(
            id = UUID.fromString(id),
            price = input.price,
            description = input.description,
        )
        return UpdateServiceResult(
            success = true,
            userErrors = listOf(),
        )
    }
}

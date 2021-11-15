package com.briolink.servicecompanyservice.api.graphql.mutation

import com.briolink.servicecompanyservice.api.service.ServiceCompanyService
import com.briolink.servicecompanyservice.api.types.CreateServiceInput
import com.briolink.servicecompanyservice.api.types.CreateServiceResult
import com.briolink.servicecompanyservice.api.types.ServiceResultData
import com.briolink.servicecompanyservice.api.types.UpdateServiceInput
import com.briolink.servicecompanyservice.api.types.UpdateServiceResult
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.util.*

@DgsComponent
class ServiceMutation(
    val serviceCompanyService: ServiceCompanyService
) {
    @DgsMutation
    fun uploadServiceImage(
        @InputArgument("id") id: String,
        @InputArgument("image") image: MultipartFile?
    ): URL? {
        return serviceCompanyService.uploadProfileImage(UUID.fromString(id), image)
    }

    @DgsMutation(field = "createService")
    fun create(
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

    @DgsMutation(field = "createServiceLocal")
    fun createServiceLocal(
        @InputArgument("companyId") companyId: String,
        @InputArgument("nameService") name: String
    ): CreateServiceResult {
        (serviceCompanyService.findByNameAndCompanyId(companyId = UUID.fromString(companyId), name = name) ?: serviceCompanyService.create(
                companyId = UUID.fromString(companyId),
                name = name,
        )).let {
            return CreateServiceResult(
                    data = ServiceResultData(id = it.id.toString(), slug = it.slug),
                    userErrors = listOf(),
            )
        }
    }

    @DgsMutation(field = "updateService")
    fun update(
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

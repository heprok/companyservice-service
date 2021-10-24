package com.briolink.servicecompanyservice.api.rest

import com.briolink.servicecompanyservice.api.exception.FileTypeException
import com.briolink.servicecompanyservice.api.service.AwsS3Service
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1")
class UploadController(val s3Service: AwsS3Service) {
    @PostMapping("/image/upload")
//    @PreAuthorize("isAuthenticated()")
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        val key = try {
            s3Service.uploadTempImage(file)
        } catch (e: FileTypeException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }

        return ResponseEntity.ok(mapOf("key" to key))
    }
}

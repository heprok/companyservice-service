package com.briolink.servicecompanyservice.api.graphql

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.UUID

object SecurityUtil {
    val currentUserAccountId: UUID
        get() {
            val authentication: JwtAuthenticationToken = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
            return UUID.fromString(authentication.token.subject)
        }
}

package com.briolink.servicecompanyservice.common.config

import com.sun.istack.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.app-endpoints")
class AppEndpointsProperties {
    @NotNull
    lateinit var locationservice: String
}

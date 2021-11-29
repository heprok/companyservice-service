package com.briolink.servicecompanyservice.common.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.briolink.servicecompanyservice.common.service",
        "com.briolink.servicecompanyservice.common.dto",
        "com.briolink.servicecompanyservice.common.config"
    ]
)
class AutoConfiguration

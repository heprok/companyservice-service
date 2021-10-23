package com.briolink.servicecompanyservice.common

import com.briolink.servicecompanyservice.common.config.DBConfigurationProperties
import com.briolink.servicecompanyservice.common.jpa.inspector.HibernateStatementInspector
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
@EnableConfigurationProperties(DBConfigurationProperties::class)
class CommonAutoConfiguration {
    @Bean
    fun hibernatePropertiesCustomizer(hibernateStatementInspector: HibernateStatementInspector): HibernatePropertiesCustomizer =
            HibernatePropertiesCustomizer { hibernateProperties: MutableMap<String?, Any?> ->
                hibernateProperties["hibernate.session_factory.statement_inspector"] = hibernateStatementInspector
            }
}

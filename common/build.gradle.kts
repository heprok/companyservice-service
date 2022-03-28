import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    `java-library`

    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor:${Versions.SPRING_BOOT}")
    kapt("org.springframework.boot:spring-boot-configuration-processor:${Versions.SPRING_BOOT}")
    api("org.springframework.boot:spring-boot-starter-webflux")

    // FasterXML
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // Hibernate Types 55
    api("com.vladmihalcea:hibernate-types-55:${Versions.HIBERNATE_TYPES_55}")

    // IBM ICU4J
    implementation("com.ibm.icu:icu4j:${Versions.IBM_ICU4J}")

    // MapStruct
    implementation("org.mapstruct:mapstruct:${Versions.MAPSTRUCT}")
    kapt("org.mapstruct:mapstruct-processor:${Versions.MAPSTRUCT}")

    // Blazebit Persistence
    api("com.blazebit:blaze-persistence-integration-spring-data-2.4:${Versions.BLAZE_PERSISTENCE}")
    api("com.blazebit:blaze-persistence-integration-hibernate-5.6:${Versions.BLAZE_PERSISTENCE}")
    api("com.blazebit:blaze-persistence-core-impl:${Versions.BLAZE_PERSISTENCE}")
}

kapt {
    arguments {
        arg("mapstruct.verbose", "true")
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

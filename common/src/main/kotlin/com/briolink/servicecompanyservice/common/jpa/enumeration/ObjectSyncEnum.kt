package com.briolink.servicecompanyservice.common.jpa.enumeration

import com.fasterxml.jackson.annotation.JsonValue

enum class ObjectSyncEnum(@JsonValue val value: Int) {
    Company(0),
    CompanyIndustry(1),
    User(2),
    Connection(3),
    CompanyService(4),
    UserJobPosition(5);

    companion object {
        private val map = values().associateBy(ObjectSyncEnum::value)
        fun fromInt(type: Int): ObjectSyncEnum = map[type]!!
    }
}

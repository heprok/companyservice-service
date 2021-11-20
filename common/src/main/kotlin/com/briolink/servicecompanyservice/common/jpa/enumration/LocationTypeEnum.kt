package com.briolink.servicecompanyservice.common.jpa.enumration

import com.fasterxml.jackson.annotation.JsonValue

enum class LocationTypeEnum(@JsonValue val value: Int) {
    Country(1),
    State(2),
    City(3);
    companion object {
        private val map = values().associateBy(LocationTypeEnum::value)
        fun fromInt(type: Int): LocationTypeEnum = map[type]!!
    }
}

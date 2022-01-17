package com.briolink.servicecompanyservice.api.util

class StringUtil {
    companion object {
        fun trimAllSpaces(str: String) = str.trim().replace(Regex("[\\s]{2,}"), " ")
    }
}

package com.briolink.servicecompanyservice.common.jpa.converter

import java.time.Year
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class YearAttributeConverter : AttributeConverter<Year?, Short?> {
    override fun convertToDatabaseColumn(attribute: Year?): Short? = attribute?.value?.toShort()
    override fun convertToEntityAttribute(dbData: Short?): Year? = if (dbData != null) Year.of(dbData.toInt()) else null
}

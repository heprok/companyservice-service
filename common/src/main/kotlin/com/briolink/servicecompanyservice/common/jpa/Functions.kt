package com.briolink.servicecompanyservice.common.jpa

import com.briolink.servicecompanyservice.common.jpa.func.JsonbGetFunc
import com.briolink.servicecompanyservice.common.jpa.func.JsonbSetsFunc
import org.hibernate.boot.MetadataBuilder
import org.hibernate.boot.spi.MetadataBuilderContributor
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.BooleanType
import org.hibernate.type.DoubleType

class Functions : MetadataBuilderContributor {
    override fun contribute(metadataBuilder: MetadataBuilder) {
        metadataBuilder.applySqlFunction(
            "fts_partial",
            SQLFunctionTemplate(BooleanType.INSTANCE, "?1 @@ to_tsquery(quote_literal(quote_literal(?2)) || ':*')"),
        )
        metadataBuilder.applySqlFunction(
            "int4range_contains",
            SQLFunctionTemplate(BooleanType.INSTANCE, "?1 <@ int4range(?2, ?3)"),
        )
        metadataBuilder.applySqlFunction(
            "array_contains",
            SQLFunctionTemplate(BooleanType.INSTANCE, "?1 @> ?2"),
        )
        metadataBuilder.applySqlFunction("fts_rank", StandardSQLFunction("ts_rank", DoubleType.INSTANCE))
        metadataBuilder.applySqlFunction("jsonb_sets", JsonbSetsFunc())
        metadataBuilder.applySqlFunction("jsonb_get", JsonbGetFunc())
    }
}

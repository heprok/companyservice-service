//package com.briolink.servicecompanyservice.api.graphql.scalartype
//
//import com.netflix.graphql.dgs.DgsScalar
//import graphql.schema.Coercing
//import graphql.schema.CoercingParseLiteralException
//import graphql.schema.CoercingParseValueException
//import graphql.schema.CoercingSerializeException
//import java.net.URL
//import java.time.Year
//
//
//@DgsScalar(name = "DomainUrl")
//class DateTimeScalar : Coercing<URL, String> {
//    @Throws(CoercingSerializeException::class)
//    override fun serialize(dataFetcherResult: Any): String {
//        return if (dataFetcherResult is URL) {
//            println(dataFetcherResult)
//            println(dataFetcherResult.let {
//                it.host + ":" + it.host
//            })
//            dataFetcherResult.let {
//                it.host + ":" + it.host
//            }
//        } else {
//            throw CoercingSerializeException("Not a valid DateTime")
//        }
//    }
//
//    @Throws(CoercingParseValueException::class)
//    override fun parseValue(input: Any): URL {
//        return LocalDateTime.parse(input.toString(), DateTimeFormatter.ISO_DATE_TIME)
//    }
//
//    @Throws(CoercingParseLiteralException::class)
//    override fun parseLiteral(input: Any): URL {
//        if (input is StringValue) {
//            return URL
//        }
//        throw CoercingParseLiteralException("Value is not a valid ISO date time")
//    }
//}

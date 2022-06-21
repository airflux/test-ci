package io.github.airflux.std.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData

internal class ByteReaderTest : FreeSpec() {

    companion object {
        private val context: JsReaderContext = JsReaderContext(
            listOf(
                InvalidTypeErrorBuilder(JsonErrors::InvalidType),
                ValueCastErrorBuilder(JsonErrors::ValueCast)
            )
        )
    }

    init {

        "The byte type reader" - {

            "should return the byte value" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is an equal minimum of the allowed range", Byte.MIN_VALUE),
                        Pair("Value is an equal maximum of the allowed range", Byte.MAX_VALUE)
                    )
                ) { (_, value) ->
                    val input: JsValue = JsNumber.valueOf(value)
                    val result = ByteReader.read(context, JsLocation.empty, input)
                    result.assertAsSuccess(location = JsLocation.empty, value = value)
                }
            }

            "should return the invalid type error" {
                val input: JsValue = JsString("abc")
                val result = ByteReader.read(context, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.NUMBER, actual = JsValue.Type.STRING)
                    )
                )
            }

            "should return the value cast error" - {
                withData(
                    nameFn = { "${it.first}: ${it.second}" },
                    listOf(
                        Pair("Value is less than the allowed range", getLessValue()),
                        Pair("Value is more than the allowed range", getMoreValue()),
                        Pair("The value is in an invalid format, negative with a fractional part", "-10.5"),
                        Pair("The value is in an invalid format, positive with a fractional part.", "10.5"),
                    )
                ) { (_, value) ->
                    val input = JsNumber.valueOf(value)!!
                    val result = ByteReader.read(context, JsLocation.empty, input)
                    result.assertAsFailure(
                        JsResult.Failure.Cause(
                            location = JsLocation.empty,
                            error = JsonErrors.ValueCast(value = value, type = Byte::class)
                        )
                    )
                }
            }
        }
    }

    private fun getLessValue(): String = (Byte.MIN_VALUE.toInt() - 1).toString()
    private fun getMoreValue(): String = (Byte.MAX_VALUE.toInt() + 1).toString()
}
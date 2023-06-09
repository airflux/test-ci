/*
 * Copyright 2021-2023 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.serialization.core.reader.struct

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class OptionalWithDefaultPropertyReaderTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "6028b4e5-bb91-4018-97a8-732eb7084cb8"
        private const val ID_PROPERTY_DEFAULT_VALUE = "7815943d-5c55-4fe6-92f8-0be816caed78"

        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val READER: JsReader<EB, Unit, Unit, String> = DummyReader.string()
        private val DEFAULT = { _: JsReaderEnv<EB, Unit>, _: Unit -> ID_PROPERTY_DEFAULT_VALUE }
    }

    init {

        "The readOptional (with default) function" - {

            "when the element is defined" - {
                val lookup: JsLookup = JsLookup.Defined(
                    location = LOCATION.append(ID_PROPERTY_NAME),
                    value = JsString(ID_PROPERTY_VALUE)
                )

                "then should return the result of applying the reader" {
                    val result: ReadingResult<String?> = readOptional(
                        env = ENV,
                        context = CONTEXT,
                        lookup = lookup,
                        using = READER,
                        defaultValue = DEFAULT
                    )

                    result shouldBeSuccess success(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        value = ID_PROPERTY_VALUE
                    )
                }
            }

            "when the element is missing" - {
                val lookup: JsLookup =
                    JsLookup.Undefined.PathMissing(location = LOCATION.append(ID_PROPERTY_NAME))

                "then should return the default value" {
                    val result: ReadingResult<String?> = readOptional(
                        env = ENV,
                        context = CONTEXT,
                        lookup = lookup,
                        using = READER,
                        defaultValue = DEFAULT
                    )

                    result shouldBeSuccess success(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        value = ID_PROPERTY_DEFAULT_VALUE
                    )
                }
            }

            "when the element is invalid type" - {
                val lookup: JsLookup = JsLookup.Undefined.InvalidType(
                    expected = listOf(JsStruct.nameOfType),
                    actual = JsString.nameOfType,
                    breakpoint = LOCATION.append(ID_PROPERTY_NAME)
                )

                "then should return the invalid type error" {
                    val result: ReadingResult<String?> = readOptional(
                        env = ENV,
                        context = CONTEXT,
                        lookup = lookup,
                        using = READER,
                        defaultValue = DEFAULT
                    )
                    result shouldBeFailure failure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsStruct.nameOfType),
                            actual = JsString.nameOfType
                        )
                    )
                }
            }
        }
    }

    internal class EB : PathMissingErrorBuilder, InvalidTypeErrorBuilder {
        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}

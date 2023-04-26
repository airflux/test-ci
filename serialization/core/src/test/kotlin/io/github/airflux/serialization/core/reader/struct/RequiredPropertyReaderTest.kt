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

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.LookupResult
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.StructNode
import io.kotest.core.spec.style.FreeSpec

internal class RequiredPropertyReaderTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "a64d62c7-4a57-4282-bce3-3cd52b815204"

        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val READER: Reader<EB, Unit, Unit, String> = DummyReader.string()
    }

    init {

        "The readRequired function" - {

            "when the element is defined" - {
                val lookup: LookupResult = LookupResult.Defined(
                    location = LOCATION.append(ID_PROPERTY_NAME),
                    value = StringNode(ID_PROPERTY_VALUE)
                )

                "then should return the result of applying the reader" {
                    val result: ReadingResult<String?> =
                        readRequired(env = ENV, context = CONTEXT, lookup = lookup, using = READER)
                    result shouldBeSuccess ReadingResult.Success(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        value = ID_PROPERTY_VALUE
                    )
                }
            }

            "when the element is missing" - {
                val lookup: LookupResult =
                    LookupResult.Undefined.PathMissing(location = LOCATION.append(ID_PROPERTY_NAME))

                "then should return the missing path error" {
                    val result: ReadingResult<String?> =
                        readRequired(env = ENV, context = CONTEXT, lookup = lookup, using = READER)
                    result shouldBeFailure ReadingResult.Failure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.PathMissing
                    )
                }
            }

            "when the element is invalid type" - {
                val lookup: LookupResult = LookupResult.Undefined.InvalidType(
                    expected = listOf(StructNode.nameOfType),
                    actual = StringNode.nameOfType,
                    breakpoint = LOCATION.append(ID_PROPERTY_NAME)
                )

                "then should return the invalid type error" {
                    val result: ReadingResult<String?> =
                        readRequired(env = ENV, context = CONTEXT, lookup = lookup, using = READER)
                    result shouldBeFailure ReadingResult.Failure(
                        location = LOCATION.append(ID_PROPERTY_NAME),
                        error = JsonErrors.InvalidType(
                            expected = listOf(StructNode.nameOfType),
                            actual = StringNode.nameOfType
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

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

package io.github.airflux.serialization.core.reader.array

import io.github.airflux.serialization.core.common.DummyReader
import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.common.kotest.shouldBeFailure
import io.github.airflux.serialization.core.common.kotest.shouldBeSuccess
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.AdditionalItemsErrorBuilder
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.BooleanNode
import io.github.airflux.serialization.core.value.NumericNode
import io.github.airflux.serialization.core.value.StringNode
import io.kotest.core.spec.style.FreeSpec
import kotlin.reflect.KClass

internal class ArrayPropertyReaderForPrefixItemsOnlyTest : FreeSpec() {

    companion object {
        private const val FIRST_PHONE_VALUE = "123"
        private const val SECOND_PHONE_VALUE = "456"
        private const val THIRD_PHONE_VALUE = "789"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private val IntReader: Reader<EB, OPTS, Unit, Int> = DummyReader.int()
        private val StringReader: Reader<EB, OPTS, Unit, String> = DummyReader.string()
        private val BooleanReader: Reader<EB, OPTS, Unit, Boolean> = DummyReader.boolean()
    }

    init {

        "The readArray function for the prefix-items-only readers" - {

            "when parameter 'source' is empty" - {
                val source = ArrayNode()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                    "then reader should return result" {
                        val result: ReaderResult<List<String>> = readArray(
                            env = envWithFailFastIsTrue,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            errorIfAdditionalItems = true
                        )

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = emptyList())
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                    "then reader should return result" {
                        val result: ReaderResult<List<String>> = readArray(
                            env = envWithFailFastIsFalse,
                            context = CONTEXT,
                            location = LOCATION,
                            source = source,
                            prefixItemReaders = listOf(StringReader),
                            errorIfAdditionalItems = true
                        )

                        result shouldBeSuccess ReaderResult.Success(location = LOCATION, value = emptyList())
                    }
                }
            }

            "when parameter 'source' is not empty" - {
                val source = ArrayNode(
                    StringNode(FIRST_PHONE_VALUE),
                    StringNode(SECOND_PHONE_VALUE),
                    StringNode(THIRD_PHONE_VALUE)
                )

                "when the number of items is more than the number of readers" - {
                    val readers = listOf(StringReader)

                    "when errorIfAdditionalItems is true" - {
                        val errorIfAdditionalItems = true

                        "when fail-fast is true" - {
                            val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                            "then reader should return first error" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeFailure ReaderResult.Failure(
                                    location = LOCATION.append(1),
                                    error = JsonErrors.AdditionalItems
                                )
                            }
                        }

                        "when fail-fast is false" - {
                            val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                            "then reader should return all errors" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result.shouldBeFailure(
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(1),
                                        error = JsonErrors.AdditionalItems
                                    ),
                                    ReaderResult.Failure.Cause(
                                        location = LOCATION.append(2),
                                        error = JsonErrors.AdditionalItems
                                    )
                                )
                            }
                        }
                    }

                    "when errorIfAdditionalItems is false" - {
                        val errorIfAdditionalItems = false

                        "when fail-fast is true" - {
                            val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                            "then reader should return result" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsTrue,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeSuccess ReaderResult.Success(
                                    location = LOCATION,
                                    value = listOf(FIRST_PHONE_VALUE)
                                )
                            }
                        }

                        "when fail-fast is false" - {
                            val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                            "then reader should return result" {
                                val result: ReaderResult<List<String>> = readArray(
                                    env = envWithFailFastIsFalse,
                                    context = CONTEXT,
                                    location = LOCATION,
                                    source = source,
                                    prefixItemReaders = readers,
                                    errorIfAdditionalItems = errorIfAdditionalItems
                                )

                                result shouldBeSuccess ReaderResult.Success(
                                    location = LOCATION,
                                    value = listOf(FIRST_PHONE_VALUE)
                                )
                            }
                        }
                    }
                }

                "when the number of items is equal to the number of readers" - {
                    val errorIfAdditionalItems = true
                    val readers = listOf(StringReader, StringReader, StringReader)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return result" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return result" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when the number of items is less than the number of readers" - {
                    val errorIfAdditionalItems = true
                    val readers = listOf(StringReader, StringReader, StringReader, StringReader)

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return result" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return result" {
                            val result: ReaderResult<List<String>> = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = readers,
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeSuccess ReaderResult.Success(
                                location = LOCATION,
                                value = listOf(FIRST_PHONE_VALUE, SECOND_PHONE_VALUE, THIRD_PHONE_VALUE)
                            )
                        }
                    }
                }

                "when read was some errors" - {
                    val errorIfAdditionalItems = true

                    "when fail-fast is true" - {
                        val envWithFailFastIsTrue = ReaderEnv(EB(), OPTS(failFast = true))

                        "then reader should return first error" {
                            val result = readArray(
                                env = envWithFailFastIsTrue,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader),
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result shouldBeFailure ReaderResult.Failure(
                                location = LOCATION.append(0),
                                error = JsonErrors.InvalidType(
                                    expected = listOf(NumericNode.Integer.nameOfType),
                                    actual = StringNode.nameOfType
                                )
                            )
                        }
                    }

                    "when fail-fast is false" - {
                        val envWithFailFastIsFalse = ReaderEnv(EB(), OPTS(failFast = false))

                        "then reader should return all errors" {
                            val result = readArray(
                                env = envWithFailFastIsFalse,
                                context = CONTEXT,
                                location = LOCATION,
                                source = source,
                                prefixItemReaders = listOf(IntReader, StringReader, BooleanReader),
                                errorIfAdditionalItems = errorIfAdditionalItems
                            )

                            result.shouldBeFailure(
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(0),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(NumericNode.Integer.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                ),
                                ReaderResult.Failure.Cause(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.InvalidType(
                                        expected = listOf(BooleanNode.nameOfType),
                                        actual = StringNode.nameOfType
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    internal class EB : AdditionalItemsErrorBuilder,
                        InvalidTypeErrorBuilder,
                        ValueCastErrorBuilder {
        override fun additionalItemsError(): ReaderResult.Error = JsonErrors.AdditionalItems

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)

        override fun valueCastError(value: String, target: KClass<*>): ReaderResult.Error =
            JsonErrors.ValueCast(value, target)
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}
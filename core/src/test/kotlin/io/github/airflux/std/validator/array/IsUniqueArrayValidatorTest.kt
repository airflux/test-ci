/*
 * Copyright 2021-2022 Maxim Sambulat.
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

package io.github.airflux.std.validator.array

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.context.error.errorBuilderName
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.FailFast
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsArray
import io.github.airflux.core.value.JsString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsUniqueArrayValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION = JsLocation.empty
    }

    init {

        "The array validator IsUnique" - {
            val validator = ArrayValidator.isUnique(keySelector = { key: String -> key }).build()

            "when the reader context does not contain the error builder" - {
                val context = JsReaderContext()

                "when the test condition is false" {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("A"))
                    val items: List<String> = listOf("A", "A")

                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, input, items)
                    }
                    exception.message shouldBe "The error builder '${IsUniqueArrayValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = JsReaderContext(
                    IsUniqueArrayValidator.ErrorBuilder(JsonErrors.Validation.Arrays::Unique)
                )

                "when a collection is empty" - {
                    val input: JsArray<JsString> = JsArray()
                    val items: List<String> = emptyList()

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, input, items)
                        errors.shouldBeNull()
                    }
                }

                "when a collection does not contain duplicate" - {
                    val input: JsArray<JsString> = JsArray(JsString("A"), JsString("B"))
                    val items: List<String> = listOf("A", "B")

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, input, items)
                        errors.shouldBeNull()
                    }
                }

                "when a collection contains duplicate" - {
                    val input: JsArray<JsString> =
                        JsArray(JsString("A"), JsString("B"), JsString("A"), JsString("B"), JsString("C"))
                    val items: List<String> = listOf("A", "B", "A", "B", "C")

                    "and fail-fast is missing" - {

                        "then the validator should return the first error" {
                            val failure = validator.validate(context, LOCATION, input, items)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION.append(2),
                                error = JsonErrors.Validation.Arrays.Unique("A")
                            )
                        }
                    }

                    "and fail-fast is true" - {
                        val failFastContext = context + FailFast(true)

                        "then the validator should return the first error" {
                            val failure = validator.validate(failFastContext, LOCATION, input, items)

                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(
                                location = LOCATION.append(2),
                                error = JsonErrors.Validation.Arrays.Unique("A")
                            )
                        }
                    }

                    "and fail-fast is false" - {
                        val failFastContext = context + FailFast(false)

                        "then the validator should return all errors" {
                            val failure = validator.validate(failFastContext, LOCATION, input, items)

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                JsResult.Failure(
                                    location = LOCATION.append(2),
                                    error = JsonErrors.Validation.Arrays.Unique("A")
                                ),
                                JsResult.Failure(
                                    location = LOCATION.append(3),
                                    error = JsonErrors.Validation.Arrays.Unique("B")
                                )
                            ).merge()
                        }
                    }
                }
            }
        }
    }
}

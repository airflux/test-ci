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

package io.github.airflux.serialization.dsl.reader.array.builder.validator

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class ArrayValidatorTest : FreeSpec() {

    companion object {
        private val CONTEXT = ReaderContext()
        private val LOCATION = JsLocation.empty
        private val VALUE = ArrayNode<StringNode>()
    }

    init {

        "The ArrayValidator type" - {

            "composition OR operator" - {

                "when the left validator returns success" - {
                    val leftValidator = ArrayValidator { _, _, _ -> null }

                    "then the right validator does not execute" {
                        val rightValidator = ArrayValidator { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validate(CONTEXT, LOCATION, VALUE)

                        errors.shouldBeNull()
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = ArrayValidator { _, location, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "when the right validator returns success" - {
                        val rightValidator = ArrayValidator { _, _, _ -> null }

                        "then failure of the left validator is returned" {
                            val composeValidator = leftValidator or rightValidator
                            val errors = composeValidator.validate(CONTEXT, LOCATION, VALUE)

                            errors.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = ArrayValidator { _, location, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                            )
                        }

                        "then both errors are returned" {
                            val composeValidator = leftValidator or rightValidator
                            val failure = composeValidator.validate(CONTEXT, LOCATION, VALUE)

                            failure.shouldNotBeNull()
                            failure shouldBe listOf(
                                JsResult.Failure(LOCATION, JsonErrors.PathMissing),
                                JsResult.Failure(
                                    location = LOCATION,
                                    error = JsonErrors.InvalidType(
                                        expected = ValueNode.Type.STRING,
                                        actual = ValueNode.Type.BOOLEAN
                                    )
                                )
                            ).merge()
                        }
                    }
                }
            }

            "composition AND operator" - {

                "when the left validator returns success" - {
                    val leftValidator = ArrayValidator { _, _, _ -> null }

                    "when the right validator returns success" - {
                        val rightValidator = ArrayValidator { _, _, _ -> null }

                        "then success is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validate(CONTEXT, LOCATION, VALUE)
                            failure.shouldBeNull()
                        }
                    }

                    "when the right validator returns failure" - {
                        val rightValidator = ArrayValidator { _, location, _ ->
                            JsResult.Failure(location, JsonErrors.PathMissing)
                        }

                        "then failure of the right validator is returned" {
                            val composeValidator = leftValidator and rightValidator
                            val failure = composeValidator.validate(CONTEXT, LOCATION, VALUE)
                            failure.shouldNotBeNull()
                            failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                        }
                    }
                }

                "when the left validator returns failure" - {
                    val leftValidator = ArrayValidator { _, location, _ ->
                        JsResult.Failure(location, JsonErrors.PathMissing)
                    }

                    "then the right validator does not execute" - {
                        val rightValidator = ArrayValidator { _, location, _ ->
                            JsResult.Failure(
                                location,
                                JsonErrors.InvalidType(expected = ValueNode.Type.STRING, actual = ValueNode.Type.BOOLEAN)
                            )
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validate(CONTEXT, LOCATION, VALUE)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, JsonErrors.PathMissing)
                    }
                }
            }
        }
    }
}
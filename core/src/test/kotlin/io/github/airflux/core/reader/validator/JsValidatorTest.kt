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

package io.github.airflux.core.reader.validator

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class JsValidatorTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "A JsValidator type" - {

            "testing the or composite operator" - {

                "if the left validator returns success then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit> { _, _, _ -> null }

                    val rightValidator = JsValidator<Unit> { _, location, _ ->
                        JsResult.Failure(location, ValidationErrors.PathMissing)
                    }

                    val composeValidator = leftValidator or rightValidator
                    val errors = composeValidator.validation(CONTEXT, LOCATION, Unit)

                    errors.shouldBeNull()
                }

                "if the left validator returns an error" - {
                    val leftValidator = JsValidator<Unit> { _, location, _ ->
                        JsResult.Failure(location, ValidationErrors.PathMissing)
                    }

                    "and the right validator returns success then returning the first error" {
                        val rightValidator = JsValidator<Unit> { _, _, _ -> null }

                        val composeValidator = leftValidator or rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, Unit)

                        errors.shouldBeNull()
                    }

                    "and the right validator returns an error then returning both errors" {
                        val rightValidator = JsValidator<Unit> { _, location, _ ->
                            JsResult.Failure(location, ValidationErrors.InvalidType)
                        }

                        val composeValidator = leftValidator or rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, Unit)

                        failure.shouldNotBeNull()
                        failure shouldBe listOf(
                            JsResult.Failure(LOCATION, ValidationErrors.PathMissing),
                            JsResult.Failure(LOCATION, ValidationErrors.InvalidType)
                        ).merge()
                    }
                }
            }

            "testing the and composite operator" - {

                "if the left validator returns an error then the right validator doesn't execute" {
                    val leftValidator = JsValidator<Unit> { _, location, _ ->
                        JsResult.Failure(location, ValidationErrors.PathMissing)
                    }

                    val rightValidator = JsValidator<Unit> { _, location, _ ->
                        JsResult.Failure(location, ValidationErrors.InvalidType)
                    }

                    val composeValidator = leftValidator and rightValidator
                    val failure = composeValidator.validation(CONTEXT, LOCATION, Unit)

                    failure.shouldNotBeNull()
                    failure shouldBe JsResult.Failure(LOCATION, ValidationErrors.PathMissing)
                }

                "if the left validator returns a success" - {
                    val leftValidator = JsValidator<Unit> { _, _, _ -> null }

                    "and the second validator returns success, then success is returned" {
                        val rightValidator = JsValidator<Unit> { _, _, _ -> null }

                        val composeValidator = leftValidator and rightValidator
                        val errors = composeValidator.validation(CONTEXT, LOCATION, Unit)

                        errors.shouldBeNull()
                    }

                    "and the right validator returns an error, then an error is returned" {
                        val rightValidator = JsValidator<Unit> { _, location, _ ->
                            JsResult.Failure(location, ValidationErrors.PathMissing)
                        }

                        val composeValidator = leftValidator and rightValidator
                        val failure = composeValidator.validation(CONTEXT, LOCATION, Unit)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(LOCATION, ValidationErrors.PathMissing)
                    }
                }
            }
        }
    }

    private sealed class ValidationErrors : JsError {
        object PathMissing : ValidationErrors()
        object InvalidType : ValidationErrors()
    }
}

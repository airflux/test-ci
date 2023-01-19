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

package io.github.airflux.serialization.std.validator.number

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validator.Validator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MaximumNumberValidatorTest : FreeSpec() {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location.empty
        private const val MAX_VALUE: Int = 2
    }

    init {

        "The numeric validator of the maximum allowed value" - {
            val validator: Validator<EB, Unit, Unit, Int> = StdNumberValidator.maximum(MAX_VALUE)

            "when a value is less than the max allowed" - {
                val value = MAX_VALUE - 1

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, CONTEXT, LOCATION, value)
                    errors.shouldBeNull()
                }
            }

            "when a value is equal to the max allowed" - {
                val value = MAX_VALUE

                "then the validator should return the null value" {
                    val errors = validator.validate(ENV, CONTEXT, LOCATION, value)
                    errors.shouldBeNull()
                }
            }

            "when a value is more than the max allowed" - {
                val value = MAX_VALUE + 1

                "then the validator should return an error" {
                    val failure = validator.validate(ENV, CONTEXT, LOCATION, value)

                    failure.shouldNotBeNull()
                    failure shouldBe ReaderResult.Failure(
                        location = LOCATION,
                        error = JsonErrors.Validation.Numbers.Max(expected = MAX_VALUE, actual = value)
                    )
                }
            }
        }
    }

    internal class EB : MaximumNumberValidator.ErrorBuilder {
        override fun maximumNumberError(expected: Number, actual: Number): ReaderResult.Error =
            JsonErrors.Validation.Numbers.Max(expected = expected, actual = actual)
    }
}

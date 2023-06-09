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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.env.option.FailFastOption
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.ReadingResult.Failure.Companion.merge
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperty
import io.github.airflux.serialization.dsl.reader.struct.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.validation.StructValidator
import io.github.airflux.serialization.std.common.JsonErrors
import io.github.airflux.serialization.test.dummy.DummyReader
import io.github.airflux.serialization.test.kotest.shouldBeInvalid
import io.github.airflux.serialization.test.kotest.shouldBeValid
import io.kotest.core.spec.style.FreeSpec

internal class AdditionalPropertiesStructValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-name"
        private const val NAME_PROPERTY_NAME = "title"
        private const val NAME_PROPERTY_VALUE = "property-title"

        private val StringReader: JsReader<EB, OPTS, Unit, String> = DummyReader.string()
        private val CONTEXT = Unit
        private val LOCATION = JsLocation
        private val idProperty: StructProperty<EB, OPTS, Unit, String> =
            StructProperty(required(ID_PROPERTY_NAME, StringReader))
        private val PROPERTIES: StructProperties<EB, OPTS, Unit> = listOf(idProperty)
    }

    init {

        "The struct validator AdditionalProperties" - {
            val validator: StructValidator<EB, OPTS, Unit> =
                StdStructValidator.additionalProperties<EB, OPTS, Unit>().build(PROPERTIES)

            "when the struct is empty" - {
                val source = JsStruct()

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should do not return any errors" {
                        val result = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)
                        result.shouldBeValid()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should do not return any errors" {
                        val result = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)
                        result.shouldBeValid()
                    }
                }
            }

            "when the struct does not contains additional properties" - {
                val source = JsStruct(ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE))

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should do not return any errors" {
                        val result = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)
                        result.shouldBeValid()
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should do not return any errors" {
                        val result = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)
                        result.shouldBeValid()
                    }
                }
            }

            "when the struct contains additional properties" - {
                val source = JsStruct(
                    ID_PROPERTY_NAME to JsString(ID_PROPERTY_VALUE),
                    TITLE_PROPERTY_VALUE to JsString(TITLE_PROPERTY_NAME),
                    NAME_PROPERTY_VALUE to JsString(NAME_PROPERTY_NAME)
                )

                "when fail-fast is true" - {
                    val envWithFailFastIsTrue = JsReaderEnv(EB(), OPTS(failFast = true))

                    "then the validator should return first error" {
                        val result = validator.validate(envWithFailFastIsTrue, CONTEXT, LOCATION, PROPERTIES, source)

                        result shouldBeInvalid failure(
                            location = LOCATION.append(TITLE_PROPERTY_VALUE),
                            error = JsonErrors.Validation.Struct.AdditionalProperties
                        )
                    }
                }

                "when fail-fast is false" - {
                    val envWithFailFastIsFalse = JsReaderEnv(EB(), OPTS(failFast = false))

                    "then the validator should return all errors" {
                        val result = validator.validate(envWithFailFastIsFalse, CONTEXT, LOCATION, PROPERTIES, source)

                        result shouldBeInvalid listOf(
                            ReadingResult.Failure(
                                location = LOCATION.append(TITLE_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            ),
                            ReadingResult.Failure(
                                location = LOCATION.append(NAME_PROPERTY_VALUE),
                                error = JsonErrors.Validation.Struct.AdditionalProperties
                            )
                        ).merge()
                    }
                }
            }
        }
    }

    internal class EB : InvalidTypeErrorBuilder,
        PathMissingErrorBuilder,
        AdditionalPropertiesStructValidator.ErrorBuilder {

        override fun additionalPropertiesStructError(): ReadingResult.Error =
            JsonErrors.Validation.Struct.AdditionalProperties

        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)

        override fun pathMissingError(): ReadingResult.Error = JsonErrors.PathMissing
    }

    internal class OPTS(override val failFast: Boolean) : FailFastOption
}

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

package io.github.airflux.serialization.std.validator.struct

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.context.error.errorBuilderName
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ObjectNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.dsl.reader.struct.builder.property.ObjectProperties
import io.github.airflux.serialization.dsl.reader.struct.builder.validator.ObjectValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class MinPropertiesObjectValidatorTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val ID_PROPERTY_VALUE = "property-id"
        private const val NAME_PROPERTY_NAME = "name"
        private const val NAME_PROPERTY_VALUE = "property-name"
        private const val TITLE_PROPERTY_NAME = "title"
        private const val TITLE_PROPERTY_VALUE = "property-title"
        private const val MIN_PROPERTIES = 2
        private val LOCATION = Location.empty
        private val PROPERTIES: ObjectProperties = ObjectProperties(emptyList())
    }

    init {

        "The object validator MinProperties" - {
            val validator: ObjectValidator = StdObjectValidator.minProperties(MIN_PROPERTIES).build(PROPERTIES)

            "when the reader context does not contain the error builder" - {
                val context = ReaderContext()
                val input = ObjectNode()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validate(context, LOCATION, PROPERTIES, input)
                    }
                    exception.message shouldBe "The error builder '${MinPropertiesObjectValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context = ReaderContext(
                    MinPropertiesObjectValidator.ErrorBuilder(JsonErrors.Validation.Object::MinProperties)
                )

                "when the object is empty" - {
                    val input = ObjectNode()

                    "then the validator should return an error" {
                        val errors = validator.validate(context, LOCATION, PROPERTIES, input)
                        errors.shouldNotBeNull()
                        errors shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Object.MinProperties(expected = MIN_PROPERTIES, actual = 0)
                        )
                    }
                }

                "when the object contains a number of properties less than the minimum" - {
                    val input = ObjectNode(ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE))

                    "then the validator should return an error" {
                        val failure = validator.validate(context, LOCATION, PROPERTIES, input)
                        failure.shouldNotBeNull()
                        failure shouldBe ReaderResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Object.MinProperties(expected = MIN_PROPERTIES, actual = 1)
                        )
                    }
                }

                "when the object contains a number of properties equal to the minimum" - {
                    val input = ObjectNode(
                        ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                        NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE)
                    )

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, PROPERTIES, input)
                        errors.shouldBeNull()
                    }
                }

                "when the object contains a number of properties more than the minimum" - {
                    val input = ObjectNode(
                        ID_PROPERTY_NAME to StringNode(ID_PROPERTY_VALUE),
                        NAME_PROPERTY_NAME to StringNode(NAME_PROPERTY_VALUE),
                        TITLE_PROPERTY_NAME to StringNode(TITLE_PROPERTY_VALUE)
                    )

                    "then the validator should do not return any errors" {
                        val errors = validator.validate(context, LOCATION, PROPERTIES, input)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
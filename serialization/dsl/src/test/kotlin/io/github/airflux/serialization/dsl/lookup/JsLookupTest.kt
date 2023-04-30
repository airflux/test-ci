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

package io.github.airflux.serialization.dsl.lookup

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.lookup.JsLookup
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsStruct
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class JsLookupTest : FreeSpec() {

    companion object {
        private const val ID_PROPERTY_NAME = "id"
        private const val UNKNOWN_PROPERTY_NAME = "identifier"

        private const val ARRAY_INDEX = 0
        private const val UNKNOWN_ARRAY_INDEX = 1

        private const val VALUE = "16945018-22fb-48fd-ab06-0740b90929d6"

        private val LOCATION = JsLocation
    }

    init {

        "The JsLookup#div extension-function" - {

            "when lookup by a key element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = JsLookup.Defined(LOCATION, JsStruct(ID_PROPERTY_NAME to JsString(VALUE)))

                    "when the value contains the finding key" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / ID_PROPERTY_NAME
                            lookup shouldBe JsLookup.Defined(LOCATION.append(ID_PROPERTY_NAME), JsString(VALUE))
                        }
                    }

                    "when the value does not contain the finding key" - {

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined / UNKNOWN_PROPERTY_NAME
                            lookup shouldBe JsLookup.Undefined.PathMissing(LOCATION.append(UNKNOWN_PROPERTY_NAME))
                        }
                    }

                    "when the value does not contain a valid element type" - {

                        "then should return the value as an instance of type Undefined#InvalidType" {
                            val lookup = defined / ARRAY_INDEX
                            lookup shouldBe JsLookup.Undefined.InvalidType(
                                expected = listOf(JsArray.nameOfType),
                                actual = JsStruct.nameOfType,
                                breakpoint = LOCATION
                            )
                        }
                    }
                }

                "when the receiver is of type Undefined#PathMissing" - {
                    val undefined = JsLookup.Undefined.PathMissing(location = LOCATION)

                    "then should return the new instance of Undefined#PathMissing type" {
                        val lookup = undefined / ID_PROPERTY_NAME
                        lookup shouldBe JsLookup.Undefined.PathMissing(location = LOCATION.append(ID_PROPERTY_NAME))
                    }
                }

                "when the receiver is of type Undefined#InvalidType" - {
                    val undefined = JsLookup.Undefined.InvalidType(
                        expected = listOf(JsStruct.nameOfType),
                        actual = JsString.nameOfType,
                        breakpoint = LOCATION
                    )

                    "then should return the same instance of Undefined#InvalidType type" {
                        val lookup = undefined / ID_PROPERTY_NAME
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }

            "when lookup by an index element of the path" - {

                "when the receiver is of type Defined" - {
                    val defined = JsLookup.Defined(LOCATION, JsArray(JsString(VALUE)))

                    "when the value contains the finding index" - {

                        "then should return the value as an instance of type Defined" {
                            val lookup = defined / ARRAY_INDEX
                            lookup shouldBe JsLookup.Defined(LOCATION.append(ARRAY_INDEX), JsString(VALUE))
                        }
                    }

                    "when the value does not contain the finding index" - {

                        "then should return the value as an instance of type Undefined#PathMissing" {
                            val lookup = defined / UNKNOWN_ARRAY_INDEX
                            lookup shouldBe JsLookup.Undefined.PathMissing(LOCATION.append(UNKNOWN_ARRAY_INDEX))
                        }
                    }

                    "when the value does not contain a valid element type" - {

                        "then should return the value as an instance of type Undefined#InvalidType" {
                            val lookup = defined / ID_PROPERTY_NAME
                            lookup shouldBe JsLookup.Undefined.InvalidType(
                                expected = listOf(JsStruct.nameOfType),
                                actual = JsArray.nameOfType,
                                breakpoint = LOCATION
                            )
                        }
                    }
                }

                "when the receiver is of type Undefined#PathMissing" - {
                    val undefined = JsLookup.Undefined.PathMissing(location = LOCATION)

                    "then should return the new instance of Undefined#PathMissing type" {
                        val lookup = undefined / ARRAY_INDEX
                        lookup shouldBe JsLookup.Undefined.PathMissing(location = LOCATION.append(ARRAY_INDEX))
                    }
                }

                "when the receiver is of type Undefined#InvalidType" - {
                    val undefined = JsLookup.Undefined.InvalidType(
                        expected = listOf(JsArray.nameOfType),
                        actual = JsStruct.nameOfType,
                        breakpoint = LOCATION
                    )

                    "then should return the same instance of Undefined#InvalidType type" {
                        val lookup = undefined / ARRAY_INDEX
                        lookup shouldBeSameInstanceAs undefined
                    }
                }
            }
        }
    }
}

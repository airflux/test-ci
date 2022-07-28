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

package io.github.airflux.serialization.dsl.writer.array.builder

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsArray
import io.github.airflux.serialization.core.value.JsNull
import io.github.airflux.serialization.core.value.JsString
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.nullable
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsArrayWriterBuilderTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "item-1"
        private const val SECOND_ITEM = "item-2"

        private val CONTEXT = JsWriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsArrayWriterBuilder type" - {

            "when have some non-nullable items for writing to an array" - {
                val writer = arrayWriter<String> {
                    items(nonNullable(writer = DummyWriter { JsString(it) }))
                }
                val value = listOf(FIRST_ITEM, SECOND_ITEM)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should returns the array with items" {
                    result as JsArray<*>
                    result shouldBe JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                }
            }

            "when have some optional items for writing to an array" - {
                val writer = arrayWriter<String?> {
                    items(optional(writer = DummyWriter { JsString(it) }))
                }
                val value = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should returns the array with items" {
                    result as JsArray<*>
                    result shouldBe JsArray(JsString(FIRST_ITEM), JsString(SECOND_ITEM))
                }
            }

            "when have some nullable items for writing to an array" - {
                val writer = arrayWriter<String?> {
                    items(nullable(writer = DummyWriter { JsString(it) }))
                }
                val value = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)
                val result = writer.write(context = CONTEXT, location = LOCATION, value = value)

                "then should returns the array with items" {
                    result as JsArray<*>
                    result shouldBe JsArray(JsNull, JsString(FIRST_ITEM), JsNull, JsString(SECOND_ITEM), JsNull)
                }
            }

            "when no items for writing to an array" - {
                val value = emptyList<String>()

                "when the action of the writer was not set" - {
                    val writer = arrayWriter<String> {
                        items(nonNullable(writer = DummyWriter { JsString(it) }))
                    }

                    "then should returns the empty JsArray" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe JsArray<JsValue>()
                    }
                }

                "when the action of the writer was set to return empty value" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnEmptyValue()
                        items(nonNullable(writer = DummyWriter { JsString(it) }))
                    }

                    "then should returns the empty JsArray" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe JsArray<JsValue>()
                    }
                }

                "when the action of the writer was set to return nothing" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnNothing()
                        items(nonNullable(writer = DummyWriter { JsString(it) }))
                    }

                    "then should returns the null value" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result.shouldBeNull()
                    }
                }

                "when the action of the writer was set to return null value" - {
                    val writer = arrayWriter<String> {
                        actionIfEmpty = returnNullValue()
                        items(nonNullable(writer = DummyWriter { JsString(it) }))
                    }

                    "then should returns the JsNull" {
                        val result = writer.write(context = CONTEXT, location = LOCATION, value = value)
                        result shouldBe JsNull
                    }
                }
            }
        }
    }
}
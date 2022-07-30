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

package io.github.airflux.serialization.dsl.writer.`object`.builder.property

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.ObjectPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ObjectWriterPropertiesBuilderInstanceTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "attribute-id"
        private const val ATTRIBUTE_VALUE = "f12720c8-a441-4b18-9783-b8bc7b31607c"

        private val CONTEXT = WriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The ObjectWriterPropertiesBuilderInstance type" - {

            "when no property is added to the builder" - {
                val properties = ObjectWriterPropertiesBuilderInstance<String>().build()

                "should be empty" {
                    properties shouldContainExactly emptyList()
                }
            }

            "when a non-nullable property were added to the builder" - {
                val from: (String) -> String = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = ObjectPropertySpec.NonNullable(name = ATTRIBUTE_NAME, from = from, writer = writer)
                val properties: ObjectProperties<String> = ObjectWriterPropertiesBuilderInstance<String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the non-nullable property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.NonNullable<*, *>>()
                        it.name shouldBe spec.name
                        it.write(CONTEXT, LOCATION, ATTRIBUTE_VALUE) shouldBe StringNode(ATTRIBUTE_VALUE)
                    }
                }
            }

            "when a optional property were added to the builder" - {
                val from: (String) -> String? = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = ObjectPropertySpec.Optional(name = ATTRIBUTE_NAME, from = from, writer = writer)
                val properties: ObjectProperties<String> = ObjectWriterPropertiesBuilderInstance<String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the optional property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.Optional<*, *>>()
                        it.name shouldBe spec.name
                        it.write(CONTEXT, LOCATION, ATTRIBUTE_VALUE) shouldBe StringNode(ATTRIBUTE_VALUE)
                    }
                }
            }

            "when a nullable property were added to the builder" - {
                val from: (String) -> String? = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = ObjectPropertySpec.Nullable(name = ATTRIBUTE_NAME, from = from, writer = writer)
                val properties: ObjectProperties<String> = ObjectWriterPropertiesBuilderInstance<String>()
                    .apply {
                        property(spec)
                    }
                    .build()

                "then the nullable property should contain in the container" {
                    properties.forOne {
                        it.shouldBeInstanceOf<ObjectProperty.Nullable<*, *>>()
                        it.name shouldBe spec.name
                        it.write(CONTEXT, LOCATION, ATTRIBUTE_VALUE) shouldBe StringNode(ATTRIBUTE_VALUE)
                    }
                }
            }
        }
    }
}
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
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification.ObjectPropertySpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class JsObjectNullablePropertyTest : FreeSpec() {

    companion object {
        private const val ATTRIBUTE_NAME = "id"
        private const val ATTRIBUTE_VALUE = "205424cf-2ebf-4b65-b3c3-7c848dc8f343"

        private val LOCATION = JsLocation.empty
    }

    init {

        "The JsObjectProperty#Nullable type" - {

            "when created an instance of the nullable property" - {
                val from: (String) -> String? = { it }
                val writer = DummyWriter<String> { StringNode(it) }
                val spec = ObjectPropertySpec.Optional(name = ATTRIBUTE_NAME, from = from, writer = writer)
                val property = JsObjectProperty.Optional(spec)

                "then the attribute name should equal the attribute name from the spec" {
                    property.name shouldBe spec.name
                }
            }

            "when the extractor returns the null value" - {
                val from: (String) -> String? = { null }
                val writer = DummyWriter<String> { StringNode(it) }
                val property = createProperty(from = from, writer = writer)

                "then the method write should return the NullNode value" {
                    val result = property.write(WriterContext(), LOCATION, ATTRIBUTE_VALUE)
                    result shouldBe NullNode
                }
            }

            "when the extractor returns the non-null value" - {
                val from: (String) -> String? = { it }

                "when the writer of the property returns the null value" - {
                    val writer = DummyWriter<String> { null }
                    val property = createProperty(from = from, writer = writer)

                    "then the method write should return the null value" {
                        val result = property.write(WriterContext(), LOCATION, ATTRIBUTE_VALUE)
                        result.shouldBeNull()
                    }
                }

                "when the writer of the property returns the not null value" - {
                    val writer = DummyWriter<String> { StringNode(it) }
                    val property = createProperty(from = from, writer = writer)

                    "then the method write should return the not null value" {
                        val result = property.write(WriterContext(), LOCATION, ATTRIBUTE_VALUE)
                        result shouldBe StringNode(ATTRIBUTE_VALUE)
                    }
                }
            }
        }
    }

    private fun <T : Any, P : Any> createProperty(
        from: (T) -> P?,
        writer: DummyWriter<P>
    ): JsObjectProperty.Nullable<T, P> =
        JsObjectProperty.Nullable(
            ObjectPropertySpec.Nullable(name = ATTRIBUTE_NAME, from = from, writer = writer)
        )
}

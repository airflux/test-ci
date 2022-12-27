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

package io.github.airflux.serialization.dsl.writer.struct.builder.property.specification

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.dsl.writer.struct.property.specification.filter
import io.github.airflux.serialization.dsl.writer.struct.property.specification.optional
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class StructOptionalPropertySpecTest : FreeSpec() {

    companion object {
        private const val PROPERTY_NAME = "id"
        private const val PROPERTY_VALUE = "89ec69f1-c636-42b8-8e62-6250c4321330"

        private val ENV = WriterEnv(context = Unit)
        private val LOCATION = Location.empty
    }

    init {

        "The StructPropertySpec#Optional type" - {

            "when created the instance of a spec of the nullable property" - {
                val from: (String) -> String? = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = optional(name = PROPERTY_NAME, from = from, writer = writer)

                "then the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equals the passed the value extractor" {
                    spec.from shouldBe from
                }

                "then the initialized writer should return a property value" {
                    val result = spec.writer.write(ENV, LOCATION, PROPERTY_VALUE)
                    result shouldBe StringNode(PROPERTY_VALUE)
                }
            }

            "when the filter was added to the spec" - {
                val from: (String) -> String? = { it }
                val writer: Writer<Unit, String> = DummyWriter { StringNode(it) }
                val spec = optional(name = "id", from = from, writer = writer)
                val specWithFilter = spec.filter { _, _, value -> value.isNotEmpty() }

                "then the property name should equal the passed property name" {
                    spec.name shouldBe PROPERTY_NAME
                }

                "then the value extractor should equals the passed the value extractor" {
                    spec.from shouldBe from
                }

                "when passing a value that satisfies the predicate for filtering" - {
                    val result = specWithFilter.writer.write(ENV, LOCATION, PROPERTY_VALUE)

                    "then a non-null property value should be returned" {
                        result shouldBe StringNode(PROPERTY_VALUE)
                    }
                }

                "when passing a value that does not satisfy the filter predicate" - {
                    val result = specWithFilter.writer.write(ENV, LOCATION, "")

                    "then the null value should be returned" {
                        result.shouldBeNull()
                    }
                }
            }
        }
    }
}

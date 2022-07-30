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

package io.github.airflux.serialization.dsl.writer.array.builder.item

import io.github.airflux.serialization.common.DummyWriter
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.JsArrayItemSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class JsArrayNonNullableItemsTest : FreeSpec() {

    companion object {
        private const val ITEM_VALUE = "value"

        private val CONTEXT = WriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {
        "The JsArrayItems#NonNullable" - {

            "when created an instance of the non-nullable item" - {
                val writer = DummyWriter<String> { StringNode(it) }
                val items: JsArrayItems.NonNullable<String> = createItems(writer = writer)

                "when an item is the not null value" - {
                    val value = "value"

                    "then the method write should return the null value" {
                        val result = items.write(CONTEXT, LOCATION, value)
                        result shouldBe StringNode(ITEM_VALUE)
                    }
                }
            }
        }
    }

    private fun <T : Any> createItems(writer: DummyWriter<T>): JsArrayItems.NonNullable<T> =
        JsArrayItems.NonNullable(JsArrayItemSpec.NonNullable(writer = writer))
}

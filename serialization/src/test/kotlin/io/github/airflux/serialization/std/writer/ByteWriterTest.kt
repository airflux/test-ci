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

package io.github.airflux.serialization.std.writer

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.NumberNode
import io.github.airflux.serialization.core.writer.context.WriterContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ByteWriterTest : FreeSpec() {

    companion object {
        private val CONTEXT = WriterContext()
        private val LOCATION = JsLocation.empty
    }

    init {

        "The byte type writer" - {
            val value: Byte = Byte.MAX_VALUE

            "should return the NumberNode value" {
                val result = ByteWriter.write(CONTEXT, LOCATION, value)
                result shouldBe NumberNode.valueOf(value)
            }
        }
    }
}

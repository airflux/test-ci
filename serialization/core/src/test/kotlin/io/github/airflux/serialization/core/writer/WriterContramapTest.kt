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

package io.github.airflux.serialization.core.writer

import io.github.airflux.serialization.core.common.DummyWriter
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class WriterContramapTest : FreeSpec() {

    companion object {
        private const val ID_VALUE = "89ec69f1-c636-42b8-8e62-6250c4321330"

        private val ENV = WriterEnv(options = Unit)
        private val CONTEXT = Unit
        private val LOCATION = Location
    }

    init {
        "The Writer type" - {
            val writer = DummyWriter.string<Unit, Unit>().contramap { value: Id -> value.get }

            "should return the new writer" {
                val source = Id(ID_VALUE)

                val result = writer.write(ENV, CONTEXT, LOCATION, source)

                result shouldBe StringNode(ID_VALUE)
            }
        }
    }

    internal data class Id(val get: String)
}

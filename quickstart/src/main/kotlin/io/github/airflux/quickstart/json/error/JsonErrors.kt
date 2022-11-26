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

package io.github.airflux.quickstart.json.error

import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.ValueNode

sealed class JsonErrors : ReaderResult.Error {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: ValueNode.Type, val actual: ValueNode.Type) : JsonErrors()

    data class EnumCast(val expected: String, val actual: String) : JsonErrors()

    object AdditionalItems : Validation.Arrays()

    sealed class Validation : JsonErrors() {

        sealed class Numbers : Validation() {
            class Ne<T>(val expected: T, val actual: T) : Numbers()
            class Gt<T>(val expected: T, val actual: T) : Numbers()
        }

        sealed class Arrays : Validation() {
            object IsEmpty : Arrays()
        }

        sealed class Strings : Validation() {
            object IsBlank : Strings()
        }

        sealed class Object : Validation() {
            object IsEmpty : Object()
            object AdditionalProperties : Object()
        }
    }
}

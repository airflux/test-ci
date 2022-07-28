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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.value.JsValue
import kotlin.reflect.KClass

internal sealed class JsonErrors : JsError {

    object PathMissing : JsonErrors()

    data class InvalidType(val expected: JsValue.Type, val actual: JsValue.Type) : JsonErrors()

    data class ValueCast(val value: String, val type: KClass<*>) : JsonErrors()

    object AdditionalItems : JsonErrors()

    sealed class Validation : JsonErrors() {

        sealed class Object : Validation() {
            object AdditionalProperties : Object()
            object IsEmpty : Object()
            data class MinProperties(val expected: Int, val actual: Int) : Object()
            data class MaxProperties(val expected: Int, val actual: Int) : Object()
        }

        sealed class Arrays : Validation() {
            object IsEmpty : Arrays()
            data class MinItems(val expected: Int, val actual: Int) : Arrays()
            data class MaxItems(val expected: Int, val actual: Int) : Arrays()
            data class Unique<T>(val value: T) : Arrays()
        }

        sealed class Numbers : Validation() {
            data class Min(val expected: Number, val actual: Number) : Numbers()
            data class Max(val expected: Number, val actual: Number) : Numbers()
            data class Eq(val expected: Number, val actual: Number) : Numbers()
            data class Ne(val expected: Number, val actual: Number) : Numbers()
            data class Gt(val expected: Number, val actual: Number) : Numbers()
            data class Ge(val expected: Number, val actual: Number) : Numbers()
            data class Lt(val expected: Number, val actual: Number) : Numbers()
            data class Le(val expected: Number, val actual: Number) : Numbers()
        }

        sealed class Strings : Validation() {
            data class MinLength(val expected: Int, val actual: Int) : Strings()
            data class MaxLength(val expected: Int, val actual: Int) : Strings()
            data class Pattern(val value: String, val regex: Regex) : Strings()
            data class IsA(val value: String) : Strings()
            object IsEmpty : Strings()
            object IsBlank : Strings()
        }
    }
}
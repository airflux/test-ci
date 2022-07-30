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

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.dsl.reader.array.builder.validator.ArrayValidator
import io.github.airflux.serialization.dsl.reader.array.builder.validator.JsArrayValidatorBuilder

internal class DummyArrayValidatorBuilder(
    override val key: JsArrayValidatorBuilder.Key<*>,
    result: JsResult.Failure?
) : JsArrayValidatorBuilder {

    val validator = Validator(result)
    override fun build(): ArrayValidator = validator

    internal class Validator(val result: JsResult.Failure?) : ArrayValidator {
        override fun validate(context: ReaderContext, location: JsLocation, input: ArrayNode<*>): JsResult.Failure? =
            result
    }

    companion object {
        fun <E : JsArrayValidatorBuilder> key() = object : JsArrayValidatorBuilder.Key<E> {}
    }
}

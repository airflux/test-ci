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

package io.github.airflux.dsl.reader.array.validator.std

import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsArray
import io.github.airflux.dsl.reader.array.validator.JsArrayValidator
import io.github.airflux.dsl.reader.array.validator.JsArrayValidatorBuilder

public class IsUniqueArrayValidator<T, K : Any> internal constructor(
    private val keySelector: (T) -> K
) : JsArrayValidator.After<T> {

    override fun validation(
        context: JsReaderContext,
        location: JsLocation,
        input: JsArray<*>,
        items: List<T>
    ): JsResult.Failure? {
        val failFast = context.failFast
        val errorBuilder = context.getValue(ErrorBuilder)

        val failures = mutableListOf<JsResult.Failure>()
        val unique = mutableSetOf<K>()
        items.forEachIndexed { index, item ->
            val key = keySelector(item)
            if (!unique.add(key)) {
                val failure = JsResult.Failure(location = location.append(index), errorBuilder.build(key))
                if (failFast) return failure
                failures.add(failure)
            }
        }
        return if (failures.isNotEmpty()) failures.merge() else null
    }

    public class ErrorBuilder(private val function: (value: Any) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        public fun build(value: Any): JsError = function(value)

        public companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "IsUniqueArrayValidatorErrorBuilder"
        }
    }
}

internal class IsUniqueArrayValidatorBuilder<T, K : Any>(
    private val keySelector: (T) -> K
) : JsArrayValidatorBuilder.After<T> {

    override fun build(): JsArrayValidator.After<T> = IsUniqueArrayValidator<T, K>(keySelector)
}
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

package io.github.airflux.serialization.core.reader

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.ReaderContext
import io.github.airflux.serialization.core.reader.predicate.JsPredicate
import io.github.airflux.serialization.core.reader.result.JsError
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.filter
import io.github.airflux.serialization.core.reader.result.recovery
import io.github.airflux.serialization.core.reader.result.validation
import io.github.airflux.serialization.core.reader.validator.Validator
import io.github.airflux.serialization.core.value.ValueNode

@Suppress("unused")
public fun interface Reader<out T> {

    /**
     * Convert the [ValueNode] into a T
     */
    public fun read(context: ReaderContext, location: JsLocation, input: ValueNode): JsResult<T>

    /**
     * Create a new [Reader] which maps the value produced by this [Reader].
     *
     * @param[R] The type of the value produced by the new [Reader].
     * @param transform the function applied on the result of the current instance,
     * if successful
     * @return A new [Reader] with the updated behavior.
     */
    public infix fun <R> map(transform: (T) -> R): Reader<R> =
        Reader { context, location, input -> read(context, location, input).map(transform) }
}

/**
 * Creates a new [Reader], based on this one, which first executes this
 * [Reader] logic then, if this [Reader] resulted in a [JsError], runs
 * the other [Reader] on the [ValueNode].
 *
 * @param other the [Reader] to run if this one gets a [JsError]
 * @return A new [Reader] with the updated behavior.
 */
public infix fun <T> Reader<T>.or(other: Reader<T>): Reader<T> = Reader { context, location, input ->
    read(context, location, input)
        .recovery { failure ->
            other.read(context, location, input)
                .recovery { alternative -> failure + alternative }
        }
}

public infix fun <T> Reader<T?>.filter(predicate: JsPredicate<T>): Reader<T?> =
    Reader { context, location, input ->
        this@filter.read(context, location, input)
            .filter(context, predicate)
    }

public infix fun <T> Reader<T>.validation(validator: Validator<T>): Reader<T> =
    Reader { context, location, input ->
        this@validation.read(context, location, input)
            .validation(context, validator)
    }

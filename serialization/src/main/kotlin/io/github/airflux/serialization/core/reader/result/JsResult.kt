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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.identity
import io.github.airflux.serialization.core.location.Location

@Suppress("unused")
public sealed class JsResult<out T> {

    public companion object;

    public infix fun <R> map(transform: (T) -> R): JsResult<R> =
        flatMap { location, value -> transform(value).success(location) }

    public fun <R> flatMap(transform: (Location, T) -> JsResult<R>): JsResult<R> = fold(
        ifFailure = ::identity,
        ifSuccess = { transform(it.location, it.value) }
    )

    public data class Success<T>(val location: Location, val value: T) : JsResult<T>()

    public class Failure private constructor(public val causes: List<Cause>) : JsResult<Nothing>() {

        public constructor(location: Location, error: Error) : this(listOf(Cause(location, error)))

        public constructor(location: Location, errors: Errors) : this(listOf(Cause(location, errors)))

        public operator fun plus(other: Failure): Failure = Failure(this.causes + other.causes)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Failure && this.causes == other.causes)

        override fun hashCode(): Int = causes.hashCode()

        override fun toString(): String = buildString {
            append("Failure(causes=")
            append(causes.toString())
            append(")")
        }

        public data class Cause(val location: Location, val errors: Errors) {
            public constructor(location: Location, error: Error) : this(location, Errors(error))
        }

        public companion object {
            public fun Collection<Failure>.merge(): Failure = Failure(causes = flatMap { failure -> failure.causes })
        }
    }

    public interface Error

    public class Errors private constructor(public val items: List<Error>) {

        public operator fun plus(other: Errors): Errors = Errors(items + other.items)

        override fun equals(other: Any?): Boolean =
            this === other || (other is Errors && this.items == other.items)

        override fun hashCode(): Int = items.hashCode()

        override fun toString(): String = buildString {
            append("Errors(items=")
            append(items.toString())
            append(")")
        }

        public companion object {

            public operator fun invoke(error: Error, vararg errors: Error): Errors = if (errors.isEmpty())
                Errors(listOf(error))
            else
                Errors(listOf(error) + errors.asList())

            public operator fun invoke(errors: List<Error>): Errors? =
                errors.takeIf { it.isNotEmpty() }?.let { Errors(it) }
        }
    }
}

public inline fun <T, R> JsResult<T>.fold(
    ifFailure: (JsResult.Failure) -> R,
    ifSuccess: (JsResult.Success<T>) -> R
): R = when (this) {
    is JsResult.Success -> ifSuccess(this)
    is JsResult.Failure -> ifFailure(this)
}

public inline infix fun <T> JsResult<T>.recovery(function: (JsResult.Failure) -> JsResult<T>): JsResult<T> = fold(
    ifFailure = { function(it) },
    ifSuccess = ::identity
)

public infix fun <T> JsResult<T>.getOrElse(defaultValue: () -> T): T = fold(
    ifFailure = { defaultValue() },
    ifSuccess = { it.value }
)

public infix fun <T> JsResult<T>.orElse(defaultValue: () -> JsResult<T>): JsResult<T> = fold(
    ifFailure = { defaultValue() },
    ifSuccess = ::identity
)

public fun <T> T.success(location: Location): JsResult<T> = JsResult.Success(location, this)
public fun <E : JsResult.Error> E.failure(location: Location): JsResult<Nothing> = JsResult.Failure(location, this)

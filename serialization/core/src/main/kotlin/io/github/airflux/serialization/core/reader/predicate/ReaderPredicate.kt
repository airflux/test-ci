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

package io.github.airflux.serialization.core.reader.predicate

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv

public fun interface ReaderPredicate<EB, O, CTX, T : Any> {
    public fun test(env: ReaderEnv<EB, O>, context: CTX, location: Location, value: T): Boolean
}

public infix fun <EB, O, CTX, T : Any> ReaderPredicate<EB, O, CTX, T>.or(
    alt: ReaderPredicate<EB, O, CTX, T>
): ReaderPredicate<EB, O, CTX, T> {
    val self = this
    return ReaderPredicate { env, context, location, value ->
        self.test(env, context, location, value) || alt.test(env, context, location, value)
    }
}

public infix fun <EB, O, CTX, T : Any> ReaderPredicate<EB, O, CTX, T>.and(
    alt: ReaderPredicate<EB, O, CTX, T>
): ReaderPredicate<EB, O, CTX, T> {
    val self = this
    return ReaderPredicate { env, context, location, value ->
        self.test(env, context, location, value) && alt.test(env, context, location, value)
    }
}
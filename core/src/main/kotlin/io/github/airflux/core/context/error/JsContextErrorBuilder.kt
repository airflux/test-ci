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

package io.github.airflux.core.context.error

import io.github.airflux.core.context.JsContext
import kotlin.reflect.full.companionObject

public operator fun <E : JsContextErrorBuilderElement> JsContext.get(key: JsContextErrorBuilderKey<E>): E =
    getOrNull(key)
        ?: throw NoSuchElementException("The error builder '${key.name}' is missing in the context.")

public interface JsContextErrorBuilderKey<E : JsContextErrorBuilderElement> : JsContext.Key<E> {
    public val name: String
}

public interface JsContextErrorBuilderElement : JsContext.Element {
    override val key: JsContextErrorBuilderKey<*>
}

public inline fun <reified T : JsContextErrorBuilderKey<*>> T.errorBuilderName(): String =
    errorBuilderName(T::class.java)

public fun <T : JsContextErrorBuilderKey<*>> errorBuilderName(javaClass: Class<T>): String =
    (javaClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == javaClass } ?: javaClass).canonicalName

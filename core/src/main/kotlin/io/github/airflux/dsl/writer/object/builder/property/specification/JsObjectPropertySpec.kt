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

package io.github.airflux.dsl.writer.`object`.builder.property.specification

import io.github.airflux.core.writer.JsWriter
import io.github.airflux.core.writer.predicate.JsPredicate

public sealed interface JsObjectPropertySpec<T, P> {
    public val name: String
    public val writer: JsWriter<P>

    public sealed interface Required<T : Any, P> : JsObjectPropertySpec<T, P> {
        public val from: (T) -> P
    }

    public sealed interface Optional<T : Any, P> : JsObjectPropertySpec<T, P?> {
        public val from: (T) -> P?

        public infix fun filter(predicate: JsPredicate<P>): Optional<T, P>
    }

    public sealed interface Nullable<T : Any, P> : JsObjectPropertySpec<T, P?> {
        public val from: (T) -> P?

        public infix fun filter(predicate: JsPredicate<P>): Nullable<T, P>
    }
}
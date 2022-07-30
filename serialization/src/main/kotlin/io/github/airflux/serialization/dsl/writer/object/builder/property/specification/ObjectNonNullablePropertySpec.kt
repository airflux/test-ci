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

package io.github.airflux.serialization.dsl.writer.`object`.builder.property.specification

import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.filter
import io.github.airflux.serialization.core.writer.predicate.WriterPredicate

public fun <T : Any, P : Any> nonNullable(
    name: String,
    from: (T) -> P,
    writer: Writer<P>
): ObjectPropertySpec.NonNullable<T, P> =
    ObjectPropertySpec.NonNullable(name = name, from = from, writer = writer)

public infix fun <T : Any, P : Any> ObjectPropertySpec.NonNullable<T, P>.filter(
    predicate: WriterPredicate<P>
): ObjectPropertySpec.Optional<T, P> =
    ObjectPropertySpec.Optional(name = name, from = from, writer = writer.filter(predicate))

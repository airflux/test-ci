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

package io.github.airflux.serialization.dsl.writer.array.builder.item

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.writer.JsWriter
import io.github.airflux.serialization.core.writer.context.JsWriterContext
import io.github.airflux.serialization.core.writer.`object`.writeNonNullable
import io.github.airflux.serialization.core.writer.`object`.writeNullable
import io.github.airflux.serialization.core.writer.`object`.writeOptional
import io.github.airflux.serialization.dsl.writer.array.builder.item.specification.JsArrayItemSpec

public sealed class JsArrayItems<T> {

    public abstract fun write(context: JsWriterContext, location: JsLocation, value: T): JsValue?

    public class NonNullable<T : Any> private constructor(private val writer: JsWriter<T>) : JsArrayItems<T>() {

        internal constructor(spec: JsArrayItemSpec.NonNullable<T>) : this(spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, value: T): JsValue? =
            writeNonNullable(context = context, location = location, using = writer, value = value)
    }

    public class Optional<T> private constructor(private val writer: JsWriter<T & Any>) : JsArrayItems<T>() {

        internal constructor(spec: JsArrayItemSpec.Optional<T>) : this(spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, value: T): JsValue? =
            writeOptional(context = context, location = location, using = writer, value = value)
    }

    public class Nullable<T> private constructor(private val writer: JsWriter<T & Any>) : JsArrayItems<T>() {

        internal constructor(spec: JsArrayItemSpec.Nullable<T>) : this(spec.writer)

        override fun write(context: JsWriterContext, location: JsLocation, value: T): JsValue? =
            writeNullable(context = context, location = location, using = writer, value = value)
    }
}

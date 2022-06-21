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

package io.github.airflux.dsl.reader.`object`.builder.property.specification

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.path.JsPaths
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.`object`.readNullable
import io.github.airflux.core.reader.or
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.result.filter
import io.github.airflux.core.reader.result.validation
import io.github.airflux.core.reader.validator.JsValidator

internal class JsObjectNullableWithDefaultPropertySpec<T : Any> private constructor(
    override val path: JsPaths,
    override val reader: JsReader<T?>
) : JsObjectPropertySpec.NullableWithDefault<T> {

    override fun validation(validator: JsValidator<T?>): JsObjectPropertySpec.NullableWithDefault<T> =
        JsObjectNullableWithDefaultPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun filter(predicate: JsPredicate<T>): JsObjectPropertySpec.NullableWithDefault<T> =
        JsObjectNullableWithDefaultPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).filter(context, predicate)
            }
        )

    override fun or(alt: JsObjectPropertySpec.NullableWithDefault<T>): JsObjectPropertySpec.NullableWithDefault<T> =
        JsObjectNullableWithDefaultPropertySpec(path = path.append(alt.path), reader = reader or alt.reader)

    companion object {

        fun <T : Any> of(
            path: JsPath,
            reader: JsReader<T>,
            default: () -> T
        ): JsObjectPropertySpec.NullableWithDefault<T> =
            JsObjectNullableWithDefaultPropertySpec(
                path = JsPaths(path),
                reader = buildReader(path, reader, default)
            )

        fun <T : Any> of(
            paths: JsPaths,
            reader: JsReader<T>,
            default: () -> T
        ): JsObjectPropertySpec.NullableWithDefault<T> =
            JsObjectNullableWithDefaultPropertySpec(
                path = paths,
                reader = paths.items
                    .map { path -> buildReader(path, reader, default) }
                    .reduce { acc, element -> acc.or(element) }
            )

        private fun <T : Any> buildReader(path: JsPath, reader: JsReader<T>, default: () -> T) =
            JsReader { context, location, input ->
                val lookup = JsLookup.apply(location, path, input)
                readNullable(context, lookup, reader, default)
            }
    }
}
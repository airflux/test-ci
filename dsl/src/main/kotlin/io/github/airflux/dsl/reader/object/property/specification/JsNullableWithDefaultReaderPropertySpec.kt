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

package io.github.airflux.dsl.reader.`object`.property.specification

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.predicate.JsPredicate
import io.github.airflux.core.reader.readNullable
import io.github.airflux.core.reader.result.extension.filter
import io.github.airflux.core.reader.validator.JsPropertyValidator
import io.github.airflux.core.reader.validator.extension.validation
import io.github.airflux.dsl.reader.`object`.property.path.JsPaths

internal class JsNullableWithDefaultReaderPropertySpec<T : Any> private constructor(
    override val path: JsPaths,
    override val reader: JsReader<T?>
) : JsReaderPropertySpec.NullableWithDefault<T> {

    constructor(path: JsPath, reader: JsReader<T>, default: () -> T, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) :
        this(
            path = JsPaths(path),
            reader = { context, location, input ->
                val lookup = JsLookup.apply(location, path, input)
                readNullable(context, lookup, reader, default, invalidTypeErrorBuilder)
            }
        )

    override fun validation(validator: JsPropertyValidator<T?>): JsReaderPropertySpec.NullableWithDefault<T> =
        JsNullableWithDefaultReaderPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).validation(context, validator)
            }
        )

    override fun filter(predicate: JsPredicate<T>): JsReaderPropertySpec.NullableWithDefault<T> =
        JsNullableWithDefaultReaderPropertySpec(
            path = path,
            reader = { context, location, input ->
                reader.read(context, location, input).filter(context, predicate)
            }
        )

    override fun or(alt: JsReaderPropertySpec.NullableWithDefault<T>): JsReaderPropertySpec.NullableWithDefault<T> =
        JsNullableWithDefaultReaderPropertySpec(path = path.append(alt.path), reader = reader or alt.reader)
}
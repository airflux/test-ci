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
import io.github.airflux.core.reader.`object`.readOptional
import io.github.airflux.core.reader.or
import io.github.airflux.core.reader.result.validate
import io.github.airflux.core.reader.validator.JsValidator

public fun <T : Any> optionalWithDefault(
    name: String,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    optionalWithDefault(JsPath(name), reader, default)

public fun <T : Any> optionalWithDefault(
    path: JsPath,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectPropertySpec.OptionalWithDefault(
        path = JsPaths(path),
        reader = { context, location, input ->
            val lookup = JsLookup.apply(location, path, input)
            readOptional(context, lookup, reader, default)
        }
    )

public fun <T : Any> optionalWithDefault(
    paths: JsPaths,
    reader: JsReader<T>,
    default: () -> T
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectPropertySpec.OptionalWithDefault(
        path = paths,
        reader = { context, location, input ->
            val lookup: JsLookup = paths.fold(
                initial = { path -> JsLookup.apply(location, path, input) },
                operation = { lookup, path ->
                    if (lookup is JsLookup.Defined) return@fold lookup
                    JsLookup.apply(location, path, input)
                }
            )
            readOptional(context, lookup, reader, default)
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.OptionalWithDefault<T>.validate(
    validator: JsValidator<T>
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectPropertySpec.OptionalWithDefault(
        path = path,
        reader = { context, location, input ->
            reader.read(context, location, input).validate(context, validator)
        }
    )

public infix fun <T : Any> JsObjectPropertySpec.OptionalWithDefault<T>.or(
    alt: JsObjectPropertySpec.OptionalWithDefault<T>
): JsObjectPropertySpec.OptionalWithDefault<T> =
    JsObjectPropertySpec.OptionalWithDefault(path = path.append(alt.path), reader = reader or alt.reader)

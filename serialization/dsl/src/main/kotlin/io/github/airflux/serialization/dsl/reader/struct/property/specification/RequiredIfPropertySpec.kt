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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.JsPath
import io.github.airflux.serialization.core.path.JsPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.serialization.core.reader.struct.readOptional
import io.github.airflux.serialization.core.reader.struct.readRequired

public fun <EB, O, CTX, T> required(
    name: String,
    reader: Reader<EB, O, CTX, T>,
    predicate: (ReaderEnv<EB, O>, CTX, Location) -> Boolean
): StructPropertySpec<EB, O, CTX, T?>
    where EB : PathMissingErrorBuilder,
          EB : InvalidTypeErrorBuilder =
    required(JsPath(name), reader, predicate)

public fun <EB, O, CTX, T> required(
    path: JsPath,
    reader: Reader<EB, O, CTX, T>,
    predicate: (ReaderEnv<EB, O>, CTX, Location) -> Boolean
): StructPropertySpec<EB, O, CTX, T?>
    where EB : PathMissingErrorBuilder,
          EB : InvalidTypeErrorBuilder =
    StructPropertySpec(
        paths = JsPaths(path),
        reader = { env, context, location, source ->
            val lookup = source.lookup(location, path)
            if (predicate(env, context, location.append(path)))
                readRequired(env, context, lookup, reader)
            else
                readOptional(env, context, lookup, reader)
        }
    )

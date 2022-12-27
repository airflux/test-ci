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

package io.github.airflux.serialization.dsl.reader.struct.property.specification

import io.github.airflux.serialization.core.lookup.lookup
import io.github.airflux.serialization.core.path.PropertyPath
import io.github.airflux.serialization.core.path.PropertyPaths
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.struct.readWithDefault

public fun <EB, CTX, T : Any> defaultable(
    name: String,
    reader: Reader<EB, CTX, T>,
    default: (ReaderEnv<EB, CTX>) -> T
): StructPropertySpec.NonNullable<EB, CTX, T> =
    defaultable(PropertyPath(name), reader, default)

public fun <EB, CTX, T : Any> defaultable(
    path: PropertyPath,
    reader: Reader<EB, CTX, T>,
    default: (ReaderEnv<EB, CTX>) -> T
): StructPropertySpec.NonNullable<EB, CTX, T> =
    StructPropertySpec.NonNullable(
        path = PropertyPaths(path),
        reader = { env, location, source ->
            val lookup = source.lookup(location, path)
            readWithDefault(env, lookup, reader, default)
        }
    )
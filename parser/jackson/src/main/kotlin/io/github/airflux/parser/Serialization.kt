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

package io.github.airflux.parser

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.serialization
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv

public fun <O, CTX, T : Any> T.serialization(
    mapper: ObjectMapper,
    env: WriterEnv<O>,
    context: CTX,
    writer: Writer<O, CTX, T>
): String? =
    this.serialization(env, context, Location, writer)
        ?.let { mapper.writeValueAsString(it) }

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
import io.github.airflux.serialization.core.deserialization
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.core.value.JsValue

public fun <EB, O, CTX, T : Any> String.deserialization(
    mapper: ObjectMapper,
    env: JsReaderEnv<EB, O>,
    context: CTX,
    reader: JsReader<EB, O, CTX, T>
): ReadingResult<T> =
    withCatching(env, JsLocation) {
        mapper.readValue(this, JsValue::class.java).deserialization(env, context, reader)
    }

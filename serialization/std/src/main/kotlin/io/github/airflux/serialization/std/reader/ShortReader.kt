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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.error.ValueCastErrorBuilder
import io.github.airflux.serialization.core.reader.result.toFailure
import io.github.airflux.serialization.core.reader.result.toSuccess
import io.github.airflux.serialization.core.value.readAsInteger

/**
 * Reader for primitive [Short] type.
 */
public fun <EB, O, CTX> shortReader(): JsReader<EB, O, CTX, Short>
    where EB : InvalidTypeErrorBuilder,
          EB : ValueCastErrorBuilder =
    JsReader { env, context, location, source ->
        source.readAsInteger(env, context, location) { e, _, l, value ->
            try {
                value.toShort().toSuccess(l)
            } catch (expected: NumberFormatException) {
                e.errorBuilders.valueCastError(value, Short::class).toFailure(location = l)
            }
        }
    }

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

package io.github.airflux.serialization.std.reader

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.JsReader
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.result.JsResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.value.JsValue
import io.github.airflux.serialization.core.value.readAsNumber
import java.math.BigDecimal

/**
 * Reader for [BigDecimal] type.
 */
public object BigDecimalReader : JsReader<BigDecimal> {
    override fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<BigDecimal> =
        input.readAsNumber(context, location) { _, p, text ->
            BigDecimal(text).success(location = p)
        }
}
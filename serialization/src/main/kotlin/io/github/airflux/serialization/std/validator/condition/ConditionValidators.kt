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

package io.github.airflux.serialization.std.validator.condition

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.context.JsReaderContext
import io.github.airflux.serialization.core.reader.validator.JsValidator

public fun <T> JsValidator<T>.applyIfNotNull(): JsValidator<T?> =
    JsValidator { context, location, value ->
        if (value != null) validate(context, location, value) else null
    }

public fun <T> JsValidator<T>.applyIf(predicate: (JsReaderContext, JsLocation, T) -> Boolean): JsValidator<T> =
    JsValidator { context, location, value ->
        if (predicate(context, location, value)) validate(context, location, value) else null
    }
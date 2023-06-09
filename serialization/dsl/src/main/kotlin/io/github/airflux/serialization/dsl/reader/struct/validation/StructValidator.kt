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

package io.github.airflux.serialization.dsl.reader.struct.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.value.JsStruct
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties

public fun interface StructValidator<EB, O, CTX> {

    public fun validate(
        env: JsReaderEnv<EB, O>,
        context: CTX,
        location: JsLocation,
        properties: StructProperties<EB, O, CTX>,
        source: JsStruct
    ): ValidationResult

    public fun interface Builder<EB, O, CTX> {
        public fun build(properties: StructProperties<EB, O, CTX>): StructValidator<EB, O, CTX>
    }
}

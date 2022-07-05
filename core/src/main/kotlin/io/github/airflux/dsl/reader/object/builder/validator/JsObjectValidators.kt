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

package io.github.airflux.dsl.reader.`object`.builder.validator

import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.validator.JsObjectValidator

public class JsObjectValidators private constructor(
    public val before: JsObjectValidator.Before?,
    public val after: JsObjectValidator.After?
) {

    @AirfluxMarker
    public class Builder internal constructor(
        public var before: JsObjectValidatorBuilder.Before? = null,
        public var after: JsObjectValidatorBuilder.After? = null
    ) {

        internal fun build(properties: JsObjectProperties): JsObjectValidators =
            JsObjectValidators(before = before?.build(properties), after = after?.build(properties))
    }
}
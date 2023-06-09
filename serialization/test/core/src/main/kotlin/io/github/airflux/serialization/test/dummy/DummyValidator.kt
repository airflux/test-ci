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

package io.github.airflux.serialization.test.dummy

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.validation.JsValidator
import io.github.airflux.serialization.core.reader.validation.ValidationResult
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

public class DummyValidator<EB, O, CTX, T>(
    public val result: (JsReaderEnv<EB, O>, CTX, JsLocation, T) -> ValidationResult
) : JsValidator<EB, O, CTX, T> {

    public constructor(result: ValidationResult) : this({ _, _, _, _ -> result })

    override fun validate(env: JsReaderEnv<EB, O>, context: CTX, location: JsLocation, value: T): ValidationResult =
        result(env, context, location, value)

    public companion object {

        @JvmStatic
        public fun <EB, O, CTX> isNotEmptyString(error: () -> ReadingResult.Error): JsValidator<EB, O, CTX, String?> =
            DummyValidator { _, _, location, value ->
                if (value != null) {
                    if (value.isNotEmpty())
                        valid()
                    else
                        invalid(location = location, error = error())
                } else
                    valid()
            }
    }
}

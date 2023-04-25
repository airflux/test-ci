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

package io.github.airflux.serialization.core.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.reader.validation.Validated
import io.github.airflux.serialization.core.reader.validation.Validator
import io.github.airflux.serialization.core.reader.validation.invalid
import io.github.airflux.serialization.core.reader.validation.valid

internal class DummyValidator<EB, O, CTX, T>(
    val result: (ReaderEnv<EB, O>, CTX, Location, T) -> Validated
) : Validator<EB, O, CTX, T> {

    constructor(result: Validated) : this({ _, _, _, _ -> result })

    override fun validate(env: ReaderEnv<EB, O>, context: CTX, location: Location, value: T): Validated =
        result(env, context, location, value)

    internal companion object {

        internal fun <EB, O, CTX> isNotEmptyString(error: () -> ReaderResult.Error): Validator<EB, O, CTX, String> =
            DummyValidator { _, _, location, value ->
                if (value.isNotEmpty())
                    valid()
                else
                    invalid(location = location, error = error())
            }
    }
}
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

package io.github.airflux.serialization.std.validator.array

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.dsl.reader.array.validator.ArrayValidator

public class MaxItemsArrayValidator<EB, O, CTX> internal constructor(
    private val expected: Int
) : ArrayValidator<EB, O, CTX>
    where EB : MaxItemsArrayValidator.ErrorBuilder {

    override fun validate(
        env: ReaderEnv<EB, O>,
        context: CTX,
        location: Location,
        source: ArrayNode
    ): ReadingResult.Failure? =
        if (source.size > expected)
            ReadingResult.Failure(location, env.errorBuilders.maxItemsArrayError(expected, source.size))
        else
            null

    public interface ErrorBuilder {
        public fun maxItemsArrayError(expected: Int, actual: Int): ReadingResult.Error
    }
}
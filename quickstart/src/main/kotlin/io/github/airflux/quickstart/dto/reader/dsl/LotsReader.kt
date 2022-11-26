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

package io.github.airflux.quickstart.dto.reader.dsl

import io.github.airflux.quickstart.dto.model.Lots
import io.github.airflux.quickstart.dto.reader.dsl.validator.CommonArrayReaderValidators
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.flatMapResult
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.result.withCatching
import io.github.airflux.serialization.dsl.reader.array.builder.arrayReader
import io.github.airflux.serialization.dsl.reader.array.builder.item.specification.nonNullable
import io.github.airflux.serialization.dsl.reader.array.builder.returns

val LotsReader: Reader<ReaderErrorBuilders, ReaderCtx, Lots> = arrayReader {
    validation {
        +CommonArrayReaderValidators
    }
    returns(items = nonNullable(LotReader))
}.flatMapResult { env, location, items ->
    withCatching(env, location) {
        Lots(items).success()
    }
}

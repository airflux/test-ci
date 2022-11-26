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

import io.github.airflux.quickstart.dto.model.Lot
import io.github.airflux.quickstart.dto.model.LotStatus
import io.github.airflux.quickstart.dto.reader.base.StringReader
import io.github.airflux.quickstart.dto.reader.base.additionalProperties
import io.github.airflux.quickstart.dto.reader.base.asEnum
import io.github.airflux.quickstart.dto.reader.base.isNotBlank
import io.github.airflux.quickstart.dto.reader.base.isNotEmpty
import io.github.airflux.quickstart.dto.reader.dsl.property.identifierPropertySpec
import io.github.airflux.quickstart.dto.reader.dsl.validator.CommonObjectReaderValidators
import io.github.airflux.quickstart.dto.reader.env.ReaderCtx
import io.github.airflux.quickstart.dto.reader.env.ReaderErrorBuilders
import io.github.airflux.serialization.core.reader.Reader
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.core.reader.validation
import io.github.airflux.serialization.dsl.reader.struct.builder.property.specification.required
import io.github.airflux.serialization.dsl.reader.struct.builder.returns
import io.github.airflux.serialization.dsl.reader.struct.builder.structReader

val LotStatusReader: Reader<ReaderErrorBuilders, ReaderCtx, LotStatus> = StringReader.validation(isNotBlank).asEnum()

val LotReader: Reader<ReaderErrorBuilders, ReaderCtx, Lot> = structReader {

    validation {
        +CommonObjectReaderValidators
        +additionalProperties
        +isNotEmpty
    }

    val id = property(identifierPropertySpec)
    val status = property(required(name = "status", reader = LotStatusReader))
    val value = property(required(name = "value", reader = ValueReader))

    returns { _, _ ->
        Lot(id = +id, status = +status, value = +value).success()
    }
}

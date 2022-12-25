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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.result.ReaderResult
import io.github.airflux.serialization.core.value.StructNode
import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidatorBuilder

internal class DummyStructValidatorBuilder<EB, CTX>(result: ReaderResult.Failure?) : StructValidatorBuilder<EB, CTX> {

    val validator = Validator<EB, CTX>(result)
    override fun build(properties: StructProperties<EB, CTX>): StructValidator<EB, CTX> = validator

    internal class Validator<EB, CTX>(val result: ReaderResult.Failure?) : StructValidator<EB, CTX> {
        override fun validate(
            env: ReaderEnv<EB, CTX>,
            location: Location,
            properties: StructProperties<EB, CTX>,
            source: StructNode
        ): ReaderResult.Failure? = result
    }

    companion object {
        internal fun <EB, CTX> additionalProperties(
            nameProperties: Set<String>,
            error: ReaderResult.Error
        ): StructValidatorBuilder<EB, CTX> =
            object : StructValidatorBuilder<EB, CTX> {
                override fun build(properties: StructProperties<EB, CTX>): StructValidator<EB, CTX> =
                    StructValidator { _, location, _, node ->
                        node.forEach { (name, _) ->
                            if (name !in nameProperties)
                                return@StructValidator ReaderResult.Failure(
                                    location = location.append(name),
                                    error = error
                                )
                        }
                        null
                    }
            }
    }
}

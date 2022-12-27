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

package io.github.airflux.serialization.dsl.reader.struct.validator.std

import io.github.airflux.serialization.dsl.reader.struct.property.StructProperties
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidator
import io.github.airflux.serialization.dsl.reader.struct.validator.StructValidatorBuilder
import io.github.airflux.serialization.std.validator.struct.MaxPropertiesStructValidator

internal class MaxPropertiesStructValidatorBuilder<EB, CTX>(private val value: Int) : StructValidatorBuilder<EB, CTX>
    where EB : MaxPropertiesStructValidator.ErrorBuilder {

    override fun build(properties: StructProperties<EB, CTX>): StructValidator<EB, CTX> =
        MaxPropertiesStructValidator(value)
}
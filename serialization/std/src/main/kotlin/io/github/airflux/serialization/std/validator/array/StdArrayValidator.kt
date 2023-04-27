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

import io.github.airflux.serialization.dsl.reader.array.validator.ArrayValidatorBuilder

public object StdArrayValidator {

    /**
     * If the is empty then an error, otherwise a success.
     */
    public fun <EB, O, CTX> isNotEmpty(): ArrayValidatorBuilder<EB, O, CTX>
        where EB : IsNotEmptyArrayValidator.ErrorBuilder =
        IsNotEmptyArrayValidatorBuilder()

    /**
     * If a number of elements in the array are less than an expected [value] then an error, otherwise a success.
     */
    public fun <EB, O, CTX> minItems(value: Int): ArrayValidatorBuilder<EB, O, CTX>
        where EB : MinItemsArrayValidator.ErrorBuilder =
        MinItemsArrayValidatorBuilder(value)

    /**
     * If a number of elements in the array are more than an expected [value] then an error, otherwise a success.
     */
    public fun <EB, O, CTX> maxItems(value: Int): ArrayValidatorBuilder<EB, O, CTX>
        where EB : MaxItemsArrayValidator.ErrorBuilder =
        MaxItemsArrayValidatorBuilder(value)
}
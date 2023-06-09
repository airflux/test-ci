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

package io.github.airflux.serialization.core.reader.validation

import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.result.ReadingResult

public sealed class ValidationResult {
    public object Valid : ValidationResult()
    public class Invalid(public val reason: ReadingResult.Failure) : ValidationResult()
}

public inline fun <T> ValidationResult.fold(ifInvalid: (ReadingResult.Failure) -> T, ifValid: () -> T): T =
    when (this) {
        is ValidationResult.Valid -> ifValid()
        is ValidationResult.Invalid -> ifInvalid(this.reason)
    }

public inline fun ValidationResult.ifInvalid(handler: (ReadingResult.Failure) -> Unit) {
    if (this is ValidationResult.Invalid) handler(this.reason)
}

public fun valid(): ValidationResult = ValidationResult.Valid

public fun invalid(location: JsLocation, error: ReadingResult.Error): ValidationResult =
    ReadingResult.Failure(location, error).toInvalid()

public fun ReadingResult.Failure.toInvalid(): ValidationResult = ValidationResult.Invalid(this)

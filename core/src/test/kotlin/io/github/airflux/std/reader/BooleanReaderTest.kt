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

package io.github.airflux.std.reader

import io.github.airflux.common.JsonErrors
import io.github.airflux.common.assertAsFailure
import io.github.airflux.common.assertAsSuccess
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue
import io.kotest.core.spec.style.FreeSpec

internal class BooleanReaderTest : FreeSpec() {

    companion object {
        private val CONTEXT = JsReaderContext(InvalidTypeErrorBuilder(JsonErrors::InvalidType))
    }

    init {

        "The boolean type reader" - {

            "should return value the true" {
                val input: JsValue = JsBoolean.valueOf(true)
                val result = BooleanReader.read(CONTEXT, JsLocation.empty, input)
                result.assertAsSuccess(location = JsLocation.empty, value = true)
            }

            "should return value the false" {
                val input: JsValue = JsBoolean.valueOf(false)
                val result = BooleanReader.read(CONTEXT, JsLocation.empty, input)
                result.assertAsSuccess(location = JsLocation.empty, value = false)
            }

            "should return the invalid type error" {
                val input: JsValue = JsString("abc")
                val result = BooleanReader.read(CONTEXT, JsLocation.empty, input)
                result.assertAsFailure(
                    JsResult.Failure.Cause(
                        location = JsLocation.empty,
                        error = JsonErrors.InvalidType(expected = JsValue.Type.BOOLEAN, actual = JsValue.Type.STRING)
                    )
                )
            }
        }
    }
}

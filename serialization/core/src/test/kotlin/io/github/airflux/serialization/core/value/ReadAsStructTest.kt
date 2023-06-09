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

package io.github.airflux.serialization.core.value

import io.github.airflux.serialization.core.common.JsonErrors
import io.github.airflux.serialization.core.location.JsLocation
import io.github.airflux.serialization.core.reader.env.JsReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReadingResult
import io.github.airflux.serialization.core.reader.result.failure
import io.github.airflux.serialization.core.reader.result.success
import io.github.airflux.serialization.test.kotest.shouldBeFailure
import io.github.airflux.serialization.test.kotest.shouldBeSuccess
import io.kotest.core.spec.style.FreeSpec

internal class ReadAsStructTest : FreeSpec() {

    companion object {
        private val ENV = JsReaderEnv(EB(), Unit)
        private val CONTEXT = Unit
        private val LOCATION = JsLocation.append("user")
        private const val USER_NAME = "user"
        private val reader = { _: JsReaderEnv<EB, Unit>, _: Unit, location: JsLocation, source: JsStruct ->
            val name = source["name"] as JsString
            success(location = location, value = DTO(name = name.get))
        }
    }

    init {
        "The 'readAsStruct' function" - {

            "when called with a receiver of the JsStruct type" - {

                "should return the DTO" {
                    val json: JsValue = JsStruct("name" to JsString(USER_NAME))
                    val result = json.readAsStruct(ENV, CONTEXT, LOCATION, reader)
                    result shouldBeSuccess success(location = LOCATION, value = DTO(name = USER_NAME))
                }
            }
            "when called with a receiver of not the JsStruct type" - {

                "should return the invalid type' error" {
                    val json: JsValue = JsBoolean.valueOf(true)
                    val result = json.readAsStruct(ENV, CONTEXT, LOCATION, reader)
                    result shouldBeFailure failure(
                        location = LOCATION,
                        error = JsonErrors.InvalidType(
                            expected = listOf(JsStruct.nameOfType),
                            actual = JsBoolean.nameOfType
                        )
                    )
                }
            }
        }
    }

    private data class DTO(val name: String)

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReadingResult.Error =
            JsonErrors.InvalidType(expected = expected, actual = actual)
    }
}

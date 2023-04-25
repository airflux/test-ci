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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.core.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.location.Location
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

internal class ReaderResultSuccessTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ELSE_VALUE = "20"

        private val LOCATION = Location.empty
    }

    init {

        "A ReaderResult#Success type" - {

            "constructor(Location, T)" {
                val result = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE)

                result.location shouldBe LOCATION
                result.value shouldBe ORIGINAL_VALUE
            }

            "should comply with equals() and hashCode() contract" {
                ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE).shouldBeEqualsContract(
                    y = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    z = ReaderResult.Success(location = LOCATION, value = ORIGINAL_VALUE),
                    others = listOf(
                        ReaderResult.Success(location = LOCATION, value = ELSE_VALUE),
                        ReaderResult.Success(location = LOCATION.append("id"), value = ORIGINAL_VALUE)
                    )
                )
            }
        }
    }
}
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

package io.github.airflux.serialization.core.reader.result

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.common.kotest.shouldBeEqualsContract
import io.github.airflux.serialization.core.value.ValueNode
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should

internal class ReaderResultErrorsTest : FreeSpec() {

    init {

        "A ReaderResult#Errors type" - {

            "#invoke(ReaderResult#Error, _) should return JsErrors with a single error" {
                val errors = ReaderResult.Errors(JsonErrors.PathMissing)

                errors.items shouldContainAll listOf(JsonErrors.PathMissing)
            }

            "#invoke(ReaderResult#Error, ReaderResult#Error) should return JsErrors with all errors" {
                val errors = ReaderResult.Errors(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING)
                )

                errors.items shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING)
                )
            }

            "#invoke(List<ReaderResult#Error>)" - {

                "should return null if list is empty" {
                    val errors = ReaderResult.Errors(emptyList())

                    errors should beNull()
                }

                "should return ReaderResult#Errors with errors from the list" {
                    val errors = ReaderResult.Errors(
                        listOf(
                            JsonErrors.PathMissing,
                            JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING)
                        )
                    )

                    errors.shouldNotBeNull()
                        .items.shouldContainAll(
                            listOf(
                                JsonErrors.PathMissing,
                                JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING)
                            )
                        )
                }
            }

            "calling plus function should return a new ReaderResult#Errors object with all errors" {
                val firstErrors = ReaderResult.Errors(JsonErrors.PathMissing)
                val secondErrors =
                    ReaderResult.Errors(JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING))

                val errors = firstErrors + secondErrors

                errors.items shouldContainAll listOf(
                    JsonErrors.PathMissing,
                    JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING)
                )
            }

            "should comply with equals() and hashCode() contract" {
                val errors = ReaderResult.Errors(JsonErrors.PathMissing)

                errors.shouldBeEqualsContract(
                    y = ReaderResult.Errors(JsonErrors.PathMissing),
                    z = ReaderResult.Errors(JsonErrors.PathMissing),
                    other = ReaderResult.Errors(JsonErrors.InvalidType(ValueNode.Type.BOOLEAN, ValueNode.Type.STRING))
                )
            }
        }
    }
}
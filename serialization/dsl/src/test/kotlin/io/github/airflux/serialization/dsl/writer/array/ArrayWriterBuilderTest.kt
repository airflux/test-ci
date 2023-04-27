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

package io.github.airflux.serialization.dsl.writer.array

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.value.ArrayNode
import io.github.airflux.serialization.core.value.NullNode
import io.github.airflux.serialization.core.value.StringNode
import io.github.airflux.serialization.core.writer.Writer
import io.github.airflux.serialization.core.writer.env.WriterEnv
import io.github.airflux.serialization.core.writer.nullable
import io.github.airflux.serialization.core.writer.optional
import io.github.airflux.serialization.dsl.common.DummyWriter
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionBuilderIfResultIsEmptyOption
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_EMPTY_VALUE
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NOTHING
import io.github.airflux.serialization.dsl.writer.env.option.WriterActionIfResultIsEmpty.RETURN_NULL_VALUE
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ArrayWriterBuilderTest : FreeSpec() {

    companion object {
        private const val FIRST_ITEM = "item-1"
        private const val SECOND_ITEM = "item-2"

        private val CONTEXT = Unit
        private val LOCATION = Location.empty

        private val StringWriter = DummyWriter.stringWriter<OPTS, Unit>()
    }

    init {

        "The ArrayWriter type" - {

            "when items is non-nullable type" - {
                val writer: Writer<OPTS, Unit, Iterable<String>> = arrayWriter(items = StringWriter)

                "when the source contains items" - {
                    val source = listOf(FIRST_ITEM, SECOND_ITEM)

                    "when the action of the writer was set to return empty value" - {
                        val action = RETURN_EMPTY_VALUE
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return a value of the ArrayNode type with items" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                        }
                    }

                    "when the action of the writer was set to return nothing" - {
                        val action = RETURN_NOTHING
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return a value of the ArrayNode type with items" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                        }
                    }

                    "when the action of the writer was set to return null value" - {
                        val action = RETURN_NULL_VALUE
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return a value of the ArrayNode type with items" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                        }
                    }
                }

                "when the source does not contain any items" - {
                    val source = emptyList<String>()

                    "when the action of the writer was set to return empty value" - {
                        val action = RETURN_EMPTY_VALUE
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return a value of the ArrayNode type without items" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe ArrayNode()
                        }
                    }

                    "when the action of the writer was set to return nothing" - {
                        val action = RETURN_NOTHING
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return the null value" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result.shouldBeNull()
                        }
                    }

                    "when the action of the writer was set to return null value" - {
                        val action = RETURN_NULL_VALUE
                        val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                        "then should return a value of the NullNode type" {
                            val result =
                                writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                            result shouldBe NullNode
                        }
                    }
                }
            }

            "when items is nullable type" - {

                "when a writer of items is nullable" - {
                    val writer: Writer<OPTS, Unit, Iterable<String?>> = arrayWriter(items = StringWriter.nullable())

                    "when the source contains only non-nullable items" - {
                        val source = listOf(FIRST_ITEM, SECOND_ITEM)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }
                    }

                    "when the source contains some nullable items" - {
                        val source = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(
                                    NullNode, StringNode(FIRST_ITEM), NullNode, StringNode(SECOND_ITEM), NullNode
                                )
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(
                                    NullNode, StringNode(FIRST_ITEM), NullNode, StringNode(SECOND_ITEM), NullNode
                                )
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(
                                    NullNode, StringNode(FIRST_ITEM), NullNode, StringNode(SECOND_ITEM), NullNode
                                )
                            }
                        }
                    }

                    "when the source contains all nullable items" - {
                        val source = listOf(null, null, null)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with NullNode items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(NullNode, NullNode, NullNode)
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with NullNode items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(NullNode, NullNode, NullNode)
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with NullNode items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(NullNode, NullNode, NullNode)
                            }
                        }
                    }

                    "when the source does not contain any items" - {
                        val source = emptyList<String>()

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type without items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode()
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return the null value" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result.shouldBeNull()
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the NullNode type" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe NullNode
                            }
                        }
                    }
                }

                "when a writer of items is optional" - {
                    val writer: Writer<OPTS, Unit, Iterable<String?>> = arrayWriter(items = StringWriter.optional())

                    "when the source contains only non-nullable items" - {
                        val source = listOf(FIRST_ITEM, SECOND_ITEM)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }
                    }

                    "when the source contains some nullable items" - {
                        val source = listOf(null, FIRST_ITEM, null, SECOND_ITEM, null)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type with items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode(StringNode(FIRST_ITEM), StringNode(SECOND_ITEM))
                            }
                        }
                    }

                    "when the source contains all nullable items" - {
                        val source = listOf(null, null, null)

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type without items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode()
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return the null value" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result.shouldBeNull()
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the NullNode type" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe NullNode
                            }
                        }
                    }

                    "when the source does not contain any items" - {
                        val source = emptyList<String>()

                        "when the action of the writer was set to return empty value" - {
                            val action = RETURN_EMPTY_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the ArrayNode type without items" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe ArrayNode()
                            }
                        }

                        "when the action of the writer was set to return nothing" - {
                            val action = RETURN_NOTHING
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return the null value" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result.shouldBeNull()
                            }
                        }

                        "when the action of the writer was set to return null value" - {
                            val action = RETURN_NULL_VALUE
                            val env = WriterEnv(options = OPTS(writerActionIfResultIsEmpty = action))

                            "then should return a value of the NullNode type" {
                                val result =
                                    writer.write(env = env, context = CONTEXT, location = LOCATION, source = source)
                                result shouldBe NullNode
                            }
                        }
                    }
                }
            }
        }
    }

    internal class OPTS(override val writerActionIfResultIsEmpty: WriterActionIfResultIsEmpty) :
        WriterActionBuilderIfResultIsEmptyOption
}
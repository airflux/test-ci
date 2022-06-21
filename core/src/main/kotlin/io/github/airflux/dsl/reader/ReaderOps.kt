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

package io.github.airflux.dsl.reader

import io.github.airflux.core.reader.JsArrayReader
import io.github.airflux.core.reader.JsObjectReader
import io.github.airflux.dsl.reader.array.builder.JsArrayReaderBuilder
import io.github.airflux.dsl.reader.config.JsArrayReaderConfig
import io.github.airflux.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.dsl.reader.`object`.builder.JsObjectReaderBuilder

public fun <T> reader(
    configuration: JsObjectReaderConfig = JsObjectReaderConfig.DEFAULT,
    block: JsObjectReaderBuilder<T>.() -> JsObjectReaderBuilder.ResultBuilder<T>
): JsObjectReader<T> {
    val readerBuilder = JsObjectReaderBuilder<T>(configuration)
    val resultBuilder = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}

public fun <T> arrayReader(
    configuration: JsArrayReaderConfig = JsArrayReaderConfig.DEFAULT,
    block: JsArrayReaderBuilder<T>.() -> JsArrayReaderBuilder.ResultBuilder<T>
): JsArrayReader<T> {
    val readerBuilder = JsArrayReaderBuilder<T>(configuration)
    val resultBuilder = readerBuilder.block()
    return readerBuilder.build(resultBuilder)
}
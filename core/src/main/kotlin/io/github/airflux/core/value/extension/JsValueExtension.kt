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

package io.github.airflux.core.value.extension

import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.value.JsBoolean
import io.github.airflux.core.value.JsNumber
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsString
import io.github.airflux.core.value.JsValue

fun JsValue.readAsBoolean(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder) =
    if (this is JsBoolean)
        JsResult.Success(location, this.get)
    else
        JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.BOOLEAN, this.type))

fun JsValue.readAsString(location: JsLocation, invalidTypeErrorBuilder: InvalidTypeErrorBuilder): JsResult<String> =
    if (this is JsString)
        JsResult.Success(location, this.get)
    else
        JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.STRING, this.type))

fun <T : Number> JsValue.readAsNumber(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, String) -> JsResult<T>
): JsResult<T> =
    if (this is JsNumber)
        reader(location, this.get)
    else
        JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.NUMBER, this.type))

fun <T> JsValue.readAsObject(
    location: JsLocation,
    invalidTypeErrorBuilder: InvalidTypeErrorBuilder,
    reader: (JsLocation, JsObject) -> JsResult<T>
): JsResult<T> =
    if (this is JsObject)
        reader(location, this)
    else
        JsResult.Failure(location = location, error = invalidTypeErrorBuilder.build(JsValue.Type.OBJECT, this.type))


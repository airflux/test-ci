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

package io.github.airflux.core.reader.context.error

import io.github.airflux.core.reader.context.JsReaderAbstractContextElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.value.JsValue

class InvalidTypeErrorBuilder(private val builder: (expected: JsValue.Type, actual: JsValue.Type) -> JsError) :
    JsReaderAbstractContextElement<InvalidTypeErrorBuilder>(key = InvalidTypeErrorBuilder),
    ErrorBuilder {

    fun build(expected: JsValue.Type, actual: JsValue.Type): JsError = builder(expected, actual)

    companion object Key : JsReaderContext.Key<InvalidTypeErrorBuilder> {
        override val name: String = "InvalidTypeErrorBuilder"
    }
}

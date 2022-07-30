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

package io.github.airflux.serialization.core.reader.context.option

import io.github.airflux.serialization.core.context.option.ContextOptionElement
import io.github.airflux.serialization.core.context.option.ContextOptionKey
import io.github.airflux.serialization.core.context.option.get
import io.github.airflux.serialization.core.reader.context.ReaderContext

public val ReaderContext.failFast: Boolean
    get() = get(FailFast) { true }

public class FailFast(public override val value: Boolean) : ContextOptionElement<Boolean> {

    override val key: ContextOptionKey<Boolean, FailFast> = Key

    public companion object Key : ContextOptionKey<Boolean, FailFast>
}

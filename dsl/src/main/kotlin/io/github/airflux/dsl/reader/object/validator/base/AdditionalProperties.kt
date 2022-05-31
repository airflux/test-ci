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

package io.github.airflux.dsl.reader.`object`.validator.base

import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.error.AbstractErrorBuilderContextElement
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsError
import io.github.airflux.core.reader.result.JsErrors
import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperties
import io.github.airflux.dsl.reader.`object`.property.JsObjectReaderProperty
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidator
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidatorBuilder

@Suppress("unused")
object AdditionalProperties : JsObjectValidatorBuilder.Before {

    override fun build(properties: JsObjectReaderProperties): JsObjectValidator.Before {
        val names: Set<String> = properties.names()
        return JsObjectValidator.Before { context, _, input ->
            val failFast = context.failFast
            val unknownProperties = mutableListOf<String>()
            input.forEach { (name, _) ->
                if (name !in names) {
                    unknownProperties.add(name)
                    if (failFast) return@forEach
                }
            }
            unknownProperties.takeIf { it.isNotEmpty() }
                ?.let {
                    val errorBuilder = context.getValue(ErrorBuilder)
                    JsErrors.of(errorBuilder.build(it))
                }
        }
    }

    internal fun JsObjectReaderProperties.names(): Set<String> {
        fun JsObjectReaderProperty.names(): List<String> = path.items
            .mapNotNull { path ->
                when (val element = path.elements.first()) {
                    is PathElement.Key -> element.get
                    is PathElement.Idx -> null
                }
            }

        return flatMap { property -> property.names() }.toSet()
    }

    class ErrorBuilder(private val function: (properties: List<String>) -> JsError) :
        AbstractErrorBuilderContextElement<ErrorBuilder>(key = ErrorBuilder) {

        fun build(properties: List<String>): JsError = function(properties)

        companion object Key : JsReaderContext.Key<ErrorBuilder> {
            override val name: String = "AdditionalPropertiesErrorBuilder"
        }
    }
}

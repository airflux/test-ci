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

package io.github.airflux.dsl.reader.`object`.builder

import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.JsObjectReader
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.context.option.failFast
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.reader.result.failure
import io.github.airflux.core.reader.result.fold
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.readAsObject
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.config.JsObjectReaderConfig
import io.github.airflux.dsl.reader.context.exception.ExceptionsHandler
import io.github.airflux.dsl.reader.`object`.builder.JsObjectReaderBuilder.ResultBuilder
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperties
import io.github.airflux.dsl.reader.`object`.builder.property.JsObjectProperty
import io.github.airflux.dsl.reader.`object`.builder.property.specification.JsObjectPropertySpec
import io.github.airflux.dsl.reader.`object`.builder.validator.JsObjectValidatorBuilder
import io.github.airflux.dsl.reader.validator.JsObjectValidator

@AirfluxMarker
public class JsObjectReaderBuilder<T> internal constructor(configuration: JsObjectReaderConfig) {

    public fun interface ResultBuilder<T> : (JsReaderContext, JsLocation, ObjectValuesMap) -> JsResult<T>

    private val validation: Validation.Builder = configuration.validation
        .let { Validation.Builder(before = it.before, after = it.after) }
    private val propertiesBuilder = JsObjectProperties.Builder()

    public fun validation(block: Validation.Builder.() -> Unit) {
        validation.block()
    }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Required<P>): JsObjectProperty.Required<P> =
        JsObjectProperty.Required(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Defaultable<P>): JsObjectProperty.Defaultable<P> =
        JsObjectProperty.Defaultable(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Optional<P>): JsObjectProperty.Optional<P> =
        JsObjectProperty.Optional(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.OptionalWithDefault<P>): JsObjectProperty.OptionalWithDefault<P> =
        JsObjectProperty.OptionalWithDefault(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.Nullable<P>): JsObjectProperty.Nullable<P> =
        JsObjectProperty.Nullable(spec)
            .also { propertiesBuilder.add(it) }

    public fun <P : Any> property(spec: JsObjectPropertySpec.NullableWithDefault<P>): JsObjectProperty.NullableWithDefault<P> =
        JsObjectProperty.NullableWithDefault(spec)
            .also { propertiesBuilder.add(it) }

    public fun returns(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): ResultBuilder<T> =
        ResultBuilder { context, location, values ->
            try {
                values.builder(context, location)
            } catch (expected: Throwable) {
                context.getOrNull(ExceptionsHandler)
                    ?.handleException(context, location, expected)
                    ?.failure(location)
                    ?: throw expected
            }
        }

    internal fun build(resultBuilder: ResultBuilder<T>): JsObjectReader<T> {
        val configuration = buildConfiguration(resultBuilder = resultBuilder)
        return JsObjectReader { context, location, input ->
            input.readAsObject(context, location) { c, l, i ->
                i.read(c, l, configuration)
            }
        }
    }

    private fun buildConfiguration(resultBuilder: ResultBuilder<T>): Configuration<T> {
        val properties: JsObjectProperties = propertiesBuilder.build()
        val validators = validation.build()
            .let {
                Configuration.Validators(
                    before = it.before?.build(properties),
                    after = it.after?.build(properties)
                )
            }
        return Configuration(
            properties = properties,
            validators = validators,
            resultBuilder = resultBuilder
        )
    }

    public class Validation private constructor(
        public val before: JsObjectValidatorBuilder.Before?,
        public val after: JsObjectValidatorBuilder.After?
    ) {

        @AirfluxMarker
        public class Builder internal constructor(
            public var before: JsObjectValidatorBuilder.Before? = null,
            public var after: JsObjectValidatorBuilder.After? = null
        ) {
            internal fun build(): Validation = Validation(before, after)
        }
    }

    internal data class Configuration<T>(
        val properties: JsObjectProperties,
        val validators: Validators,
        val resultBuilder: ResultBuilder<T>
    ) {
        internal data class Validators(
            val before: JsObjectValidator.Before?,
            val after: JsObjectValidator.After?
        )
    }

    internal companion object {

        internal fun <T> JsObject.read(
            context: JsReaderContext,
            location: JsLocation,
            configuration: Configuration<T>
        ): JsResult<T> {
            val failFast = context.failFast
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationFailure = configuration.validators.before
                ?.validation(context, location, configuration.properties, this)
            if (preValidationFailure != null) {
                if (failFast) return preValidationFailure
                failures.add(preValidationFailure)
            }

            val objectValuesMap: ObjectValuesMap = ObjectValuesMapInstance()
                .apply {
                    configuration.properties.forEach { property ->
                        this@read.read(context, location, property)
                            .fold(
                                ifFailure = { failure ->
                                    if (failFast) return failure
                                    failures.add(failure)
                                },
                                ifSuccess = { value ->
                                    this[property] = value.value
                                }
                            )
                    }
                }

            val postValidationFailure = configuration.validators.after
                ?.validation(context, location, configuration.properties, objectValuesMap, this)
            if (postValidationFailure != null) {
                if (failFast) return postValidationFailure
                failures.add(postValidationFailure)
            }

            return if (failures.isEmpty())
                configuration.resultBuilder(context, location, objectValuesMap)
            else
                failures.merge()
        }

        internal fun JsObject.read(
            context: JsReaderContext,
            location: JsLocation,
            property: JsObjectProperty
        ): JsResult<Any?> {
            fun JsObjectProperty.getReader(): JsReader<Any?> = when (this) {
                is JsObjectProperty.Required<*> -> this.reader
                is JsObjectProperty.Defaultable<*> -> this.reader
                is JsObjectProperty.Optional<*> -> this.reader
                is JsObjectProperty.OptionalWithDefault<*> -> this.reader
                is JsObjectProperty.Nullable<*> -> this.reader
                is JsObjectProperty.NullableWithDefault<*> -> this.reader
            }

            return property.getReader().read(context, location, this)
        }
    }
}

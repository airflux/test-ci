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

package io.github.airflux.dsl.reader.`object`

import io.github.airflux.core.path.JsPath
import io.github.airflux.core.reader.JsReader
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.result.JsResult.Failure.Companion.merge
import io.github.airflux.core.value.JsObject
import io.github.airflux.core.value.JsValue
import io.github.airflux.core.value.extension.readAsObject
import io.github.airflux.dsl.AirfluxMarker
import io.github.airflux.dsl.reader.`object`.ObjectReader.TypeBuilder
import io.github.airflux.dsl.reader.`object`.property.DefaultablePropertyInstance
import io.github.airflux.dsl.reader.`object`.property.JsReaderProperty
import io.github.airflux.dsl.reader.`object`.property.NullablePropertyInstance
import io.github.airflux.dsl.reader.`object`.property.NullableWithDefaultPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.OptionalPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.OptionalWithDefaultPropertyInstance
import io.github.airflux.dsl.reader.`object`.property.RequiredPropertyInstance
import io.github.airflux.dsl.reader.`object`.validator.JsObjectValidators

@Suppress("unused")
fun <T : Any> JsValue.deserialization(context: JsReaderContext = JsReaderContext(), reader: JsReader<T>): JsResult<T> =
    reader.read(context, JsLocation.empty, this)

@Suppress("unused")
class ObjectReader(
    private val globalConfiguration: ObjectReaderConfiguration = ObjectReaderConfiguration.Default,
    private val pathMissingErrorBuilder: PathMissingErrorBuilder,
    private val invalidTypeErrorBuilder: InvalidTypeErrorBuilder
) {

    operator fun <T> invoke(
        configuration: ObjectReaderConfiguration? = null,
        init: Builder<T>.() -> TypeBuilder<T>
    ): JsReader<T> {
        val builder = BuilderInstance<T>(configuration ?: globalConfiguration)
        val typeBuilder = builder.init()
        return builder.build(typeBuilder)
    }

    @AirfluxMarker
    interface Builder<T> {
        fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit)
        fun validation(init: JsObjectValidators.Builder.() -> Unit)

        fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P>
        fun <P : Any> property(path: JsPath, reader: JsReader<P>): PropertyBinder<P>

        fun build(builder: ObjectValuesMap.(JsLocation) -> JsResult<T>): TypeBuilder<T>
        fun build(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): TypeBuilder<T>
    }

    interface PropertyBinder<P : Any> {
        fun required(): JsReaderProperty.Required<P>
        fun defaultable(default: () -> P): JsReaderProperty.Defaultable<P>

        fun optional(): JsReaderProperty.Optional<P>
        fun optional(default: () -> P): JsReaderProperty.OptionalWithDefault<P>

        fun nullable(): JsReaderProperty.Nullable<P>
        fun nullable(default: () -> P): JsReaderProperty.NullableWithDefault<P>
    }

    fun interface TypeBuilder<T> : (JsReaderContext, ObjectValuesMap, JsLocation) -> JsResult<T>

    private inner class BuilderInstance<T>(
        private var configuration: ObjectReaderConfiguration
    ) : Builder<T> {

        private val validatorBuilders: JsObjectValidators.Builder = JsObjectValidators.Builder()
        private val properties = mutableListOf<JsReaderProperty>()

        override fun configuration(init: ObjectReaderConfiguration.Builder.() -> Unit) {
            configuration = ObjectReaderConfiguration.Builder(configuration).apply(init).build()
        }

        override fun validation(init: JsObjectValidators.Builder.() -> Unit) {
            validatorBuilders.apply(init)
        }

        override fun <P : Any> property(name: String, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinderInstance(JsPath(name), reader)

        override fun <P : Any> property(path: JsPath, reader: JsReader<P>): PropertyBinder<P> =
            PropertyBinderInstance(path, reader)

        override fun build(builder: ObjectValuesMap.(JsLocation) -> JsResult<T>): TypeBuilder<T> =
            TypeBuilder { _, v, p -> v.builder(p) }

        override fun build(builder: ObjectValuesMap.(JsReaderContext, JsLocation) -> JsResult<T>): TypeBuilder<T> =
            TypeBuilder { c, v, p -> v.builder(c, p) }

        fun build(typeBuilder: TypeBuilder<T>): JsReader<T> {
            val validators = validatorBuilders.build(configuration, properties)
            return JsReader { context, location, input ->
                input.readAsObject(location, invalidTypeErrorBuilder) { p, b ->
                    read(configuration, validators, properties, typeBuilder, context, p, b)
                }
            }
        }

        private inner class PropertyBinderInstance<P : Any>(
            private val path: JsPath,
            private val reader: JsReader<P>
        ) : PropertyBinder<P> {

            override fun required(): JsReaderProperty.Required<P> =
                RequiredPropertyInstance.of(path, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun defaultable(default: () -> P): JsReaderProperty.Defaultable<P> =
                DefaultablePropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(): JsReaderProperty.Optional<P> =
                OptionalPropertyInstance.of(path, reader, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun optional(default: () -> P): JsReaderProperty.OptionalWithDefault<P> =
                OptionalWithDefaultPropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(): JsReaderProperty.Nullable<P> =
                NullablePropertyInstance.of(path, reader, pathMissingErrorBuilder, invalidTypeErrorBuilder)
                    .also { registration(it) }

            override fun nullable(default: () -> P): JsReaderProperty.NullableWithDefault<P> =
                NullableWithDefaultPropertyInstance.of(path, reader, default, invalidTypeErrorBuilder)
                    .also { registration(it) }

            fun registration(property: JsReaderProperty) {
                properties.add(property)
            }
        }
    }

    companion object {

        internal fun <T> read(
            configuration: ObjectReaderConfiguration,
            validators: JsObjectValidators,
            properties: List<JsReaderProperty>,
            typeBuilder: TypeBuilder<T>,
            context: JsReaderContext,
            location: JsLocation,
            input: JsObject
        ): JsResult<T> {
            val failures = mutableListOf<JsResult.Failure>()

            val preValidationErrors = validators.before
                ?.validation(configuration, context, properties, input)
            if (preValidationErrors != null) {
                val failure = JsResult.Failure(location, preValidationErrors)
                if (configuration.failFast) return failure
                failures.add(failure)
            }

            val objectValuesMap = ObjectValuesMap.Builder(context, location, input)
                .apply {
                    properties.forEach { property ->
                        val failure = tryAddValueBy(property)
                        if (failure != null) {
                            if (configuration.failFast) return failure
                            failures.add(failure)
                        }
                    }
                }
                .build()

            val postValidationErrors = validators.after
                ?.validation(configuration, context, properties, objectValuesMap, input)
            if (postValidationErrors != null) {
                val failure = JsResult.Failure(location, postValidationErrors)
                if (configuration.failFast) return failure
                failures.add(failure)
            }

            return if (failures.isEmpty())
                typeBuilder(context, objectValuesMap, location)
            else
                failures.merge()
        }
    }
}

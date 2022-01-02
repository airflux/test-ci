package io.github.airflux.dsl.reader.`object`.property

import io.github.airflux.reader.context.JsReaderContext
import io.github.airflux.reader.result.JsLocation
import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.validator.JsPropertyValidator
import io.github.airflux.value.JsValue

sealed interface RequiredProperty<T : Any> : JsReaderProperty {

    fun read(context: JsReaderContext, location: JsLocation, input: JsValue): JsResult<T>

    fun validation(validator: JsPropertyValidator<T>): RequiredProperty<T>
}
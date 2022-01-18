package io.github.airflux.dsl.extension

import io.github.airflux.core.lookup.JsLookup
import io.github.airflux.core.path.PathElement
import io.github.airflux.core.reader.result.JsLocation
import io.github.airflux.core.value.JsValue

operator fun JsValue.div(key: String): JsLookup = JsLookup.apply(JsLocation.empty, PathElement.Key(key), this)
operator fun JsValue.div(idx: Int): JsLookup = JsLookup.apply(JsLocation.empty, PathElement.Idx(idx), this)

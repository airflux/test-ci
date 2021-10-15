package io.github.airflux.common

import io.github.airflux.reader.result.JsResult
import io.github.airflux.reader.result.JsResultPath
import kotlin.test.assertContains
import kotlin.test.assertEquals

fun <T> JsResult<T?>.assertAsSuccess(path: JsResultPath, value: T?) {
    this as JsResult.Success
    assertEquals(expected = path, actual = this.path)
    assertEquals(expected = value, actual = this.value)
}

fun JsResult<*>.assertAsFailure(vararg expected: JsResult.Failure.Cause) {

    val failures = (this as JsResult.Failure).causes

    assertEquals(expected = expected.count(), actual = failures.count(), message = "Failures more than expected.")

    expected.forEach { cause ->
        assertContains(failures, cause)
    }
}

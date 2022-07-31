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

package io.github.airflux.serialization.common

import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.result.ReaderResult
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal fun <T> ReaderResult<T?>.assertAsSuccess(location: Location, value: T?) {
    this as ReaderResult.Success
    assertEquals(expected = location, actual = this.location)
    assertEquals(expected = value, actual = this.value)
}

internal fun ReaderResult<*>.assertAsFailure(vararg expected: ReaderResult.Failure.Cause) {

    val failures = (this as ReaderResult.Failure).causes

    assertEquals(expected = expected.count(), actual = failures.count(), message = "Failures more than expected.")

    expected.forEach { cause ->
        assertContains(failures, cause)
    }
}
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

package io.github.airflux.serialization.core.reader.predicate

import io.github.airflux.serialization.common.JsonErrors
import io.github.airflux.serialization.core.location.Location
import io.github.airflux.serialization.core.reader.env.ReaderEnv
import io.github.airflux.serialization.core.reader.error.InvalidTypeErrorBuilder
import io.github.airflux.serialization.core.reader.result.ReaderResult
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ReaderPredicateOrCombinatorTest {

    companion object {
        private val ENV = ReaderEnv(EB(), Unit, Unit)
        private val LOCATION = Location.empty

        private const val MIN_VALUE = 10
        private const val MAX_VALUE = 20

        private val leftFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, value -> value < MIN_VALUE }
        private val rightFilter = ReaderPredicate<EB, Unit, Unit, Int> { _, _, value -> value > MAX_VALUE }
        private val composedFilter = leftFilter or rightFilter
    }

    @Test
    fun `The tested value is less to the minimum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MIN_VALUE - 1)
        assertTrue(result)
    }

    @Test
    fun `The tested value is equal to the minimum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MIN_VALUE)
        assertFalse(result)
    }

    @Test
    fun `The tested value is more to the minimum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MIN_VALUE + 1)
        assertFalse(result)
    }

    @Test
    fun `The tested value is less to the maximum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MAX_VALUE - 1)
        assertFalse(result)
    }

    @Test
    fun `The tested value is equal to the maximum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MAX_VALUE)
        assertFalse(result)
    }

    @Test
    fun `The tested value is more to the maximum value of the range`() {
        val result = composedFilter.test(ENV, LOCATION, MAX_VALUE + 1)
        assertTrue(result)
    }

    internal class EB : InvalidTypeErrorBuilder {
        override fun invalidTypeError(expected: Iterable<String>, actual: String): ReaderResult.Error =
            JsonErrors.InvalidType(expected, actual)
    }
}

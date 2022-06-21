package io.github.airflux.std.validator.string

import io.github.airflux.common.JsonErrors
import io.github.airflux.core.reader.context.JsReaderContext
import io.github.airflux.core.context.error.errorBuilderName
import io.github.airflux.core.location.JsLocation
import io.github.airflux.core.reader.result.JsResult
import io.github.airflux.core.reader.validator.JsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class IsNotEmptyValidatorTest : FreeSpec() {

    companion object {
        private val LOCATION: JsLocation = JsLocation.empty
    }

    init {

        "The string validator IsNotEmpty" - {
            val validator: JsValidator<String> = StringValidator.isNotEmpty

            "when the reader context does not contain the error builder" - {
                val context: JsReaderContext = JsReaderContext()

                "when the test condition is false" {
                    val exception = shouldThrow<NoSuchElementException> {
                        validator.validation(context, LOCATION, "")
                    }
                    exception.message shouldBe "The error builder '${IsNotEmptyStringValidator.ErrorBuilder.errorBuilderName()}' is missing in the context."
                }
            }

            "when the reader context contains the error builder" - {
                val context: JsReaderContext = JsReaderContext(
                    IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
                )

                "when a string is empty" - {
                    val str = ""

                    "then the validator should return an error" {
                        val failure = validator.validation(context, LOCATION, str)

                        failure.shouldNotBeNull()
                        failure shouldBe JsResult.Failure(
                            location = LOCATION,
                            error = JsonErrors.Validation.Strings.IsEmpty
                        )
                    }
                }

                "when a string is blank" - {
                    val str = " "

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }

                "when a string is not blank" - {
                    val str = "a"

                    "then the validator should return the null value" {
                        val errors = validator.validation(context, LOCATION, str)
                        errors.shouldBeNull()
                    }
                }
            }
        }
    }
}
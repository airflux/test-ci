package io.github.airflux.quickstart.dto.reader.context

import io.github.airflux.core.reader.context.error.AdditionalItemsErrorBuilder
import io.github.airflux.core.reader.context.error.InvalidTypeErrorBuilder
import io.github.airflux.core.reader.context.error.PathMissingErrorBuilder
import io.github.airflux.core.reader.context.error.ValueCastErrorBuilder
import io.github.airflux.core.reader.validator.std.string.IsAStringValidator
import io.github.airflux.core.reader.validator.std.string.IsNotBlankStringValidator
import io.github.airflux.core.reader.validator.std.string.IsNotEmptyStringValidator
import io.github.airflux.core.reader.validator.std.string.MaxLengthStringValidator
import io.github.airflux.core.reader.validator.std.string.MinLengthStringValidator
import io.github.airflux.core.reader.validator.std.string.PatternStringValidator
import io.github.airflux.dsl.reader.context.JsReaderContextBuilder
import io.github.airflux.dsl.reader.context.readerContext
import io.github.airflux.dsl.reader.`object`.validator.base.AdditionalProperties
import io.github.airflux.dsl.reader.`object`.validator.base.IsNotEmpty
import io.github.airflux.dsl.reader.`object`.validator.base.MaxProperties
import io.github.airflux.dsl.reader.`object`.validator.base.MinProperties
import io.github.airflux.quickstart.json.error.JsonErrors

val DefaultReaderContext = readerContext {
    failFast = false

    errorBuilders {
        readerErrorBuilders()
        objectValidationErrorBuilders()
        stringValidationErrorBuilders()
    }

    exceptions {
        exception<IllegalArgumentException> { _, _, _ -> JsonErrors.PathMissing }
        exception<Exception> { _, _, _ -> JsonErrors.PathMissing }
    }
}

fun JsReaderContextBuilder.ErrorsBuilder.readerErrorBuilders() {
    register(PathMissingErrorBuilder { JsonErrors.PathMissing })
    +InvalidTypeErrorBuilder(JsonErrors::InvalidType)
    +ValueCastErrorBuilder(JsonErrors::ValueCast)
    +AdditionalItemsErrorBuilder { JsonErrors.AdditionalItems }
}

fun JsReaderContextBuilder.ErrorsBuilder.objectValidationErrorBuilders() {
    +AdditionalProperties.ErrorBuilder(JsonErrors.Validation.Object::AdditionalProperties)
    +IsNotEmpty.ErrorBuilder { JsonErrors.Validation.Object.IsEmpty }
    +MinProperties.ErrorBuilder(JsonErrors.Validation.Object::MinProperties)
    +MaxProperties.ErrorBuilder(JsonErrors.Validation.Object::MaxProperties)
}

fun JsReaderContextBuilder.ErrorsBuilder.stringValidationErrorBuilders() {
    +IsNotEmptyStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsEmpty }
    +IsNotBlankStringValidator.ErrorBuilder { JsonErrors.Validation.Strings.IsBlank }
    +MinLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MinLength)
    +MaxLengthStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::MaxLength)
    +PatternStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::Pattern)
    +IsAStringValidator.ErrorBuilder(JsonErrors.Validation.Strings::IsA)
}

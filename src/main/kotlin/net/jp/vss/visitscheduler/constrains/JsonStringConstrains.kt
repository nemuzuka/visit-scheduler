package net.jp.vss.visitscheduler.constrains

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.ConstraintValidatorContext
import javax.validation.constraints.Pattern
import kotlin.annotation.MustBeDocumented
import kotlin.reflect.KClass

/**
 * JSON 文字列の validate アノテーション.
 *
 * @property message message
 * @property groups groups
 * @property payload payload
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [JsonStringConstrainsnValidator::class])
@ReportAsSingleViolation
@Pattern(regexp = ".{0,65536}", flags = [Pattern.Flag.DOTALL])
annotation class JsonStringConstrains(
    val message: String = "must match json string format",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)

/**
 * JSON 文字列の validate 本体.
 */
class JsonStringConstrainsnValidator : ConstraintValidator<JsonStringConstrains, String> {

    companion object {
        private val objectMapper = ObjectMapper()
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {

        if (value == null) return true

        return try {
            val node = objectMapper.readTree(value)
            node != null && node.isObject
        } catch (e: IOException) {
            false
        }
    }
}

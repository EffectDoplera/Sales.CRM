package com.andersenlab.crm.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates that date and time not in future excluding seconds .
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckCreateDateTimeConstraint.class)
public @interface CheckCreateDateTime {
    String message() default "Can't add incorrect date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

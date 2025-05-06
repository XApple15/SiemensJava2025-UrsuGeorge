package com.siemens.internship.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {
    String message() default "Invalid status. Allowed values: PROCESSED, NOT_PROCESSED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

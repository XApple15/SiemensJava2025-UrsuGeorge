package com.siemens.internship.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

    private static final Set<String> ALLOWED = Set.of("PROCESSED", "NOT_PROCESSED");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && ALLOWED.contains(value.toUpperCase());
    }
}

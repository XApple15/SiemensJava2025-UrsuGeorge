package com.siemens.internship.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailRegexValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_REGEX =
            "^(?![\\-\\.])[A-Za-z0-9_\\+&*-]+(?:\\.[A-Za-z0-9_\\+&*-]+)*" +
                    "@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,7}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches(EMAIL_REGEX);
    }
}

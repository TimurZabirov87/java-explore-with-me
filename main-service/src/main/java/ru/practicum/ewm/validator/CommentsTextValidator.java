package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommentsTextValidator implements ConstraintValidator<ValidCommentsText, String> {

    public void initialize(ValidCommentsText constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {

        return !(value == null || value.isEmpty() || value.isBlank());
    }
}
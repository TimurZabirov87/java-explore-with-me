package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemsToUpdateNameValidator implements ConstraintValidator<ValidItemsToUpdateName, String> {

    public void initialize(ValidItemsToUpdateName constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value == null || !(value.isEmpty() || value.isBlank()));
    }
}
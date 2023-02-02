package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemsToUpdateDescriptionValidator implements ConstraintValidator<ValidItemsToUpdateDescription, String> {

    public void initialize(ValidItemsToUpdateDescription constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value == null || !(value.isEmpty() || value.isBlank()));
    }
}
package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemsDescriptionValidator implements ConstraintValidator<ValidItemsDescription, String> {

    public void initialize(ValidItemsDescription constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {

        return !(value == null || value.isEmpty() || value.isBlank());
    }
}
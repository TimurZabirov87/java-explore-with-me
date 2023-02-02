package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemsNameValidator implements ConstraintValidator<ValidItemsName, String> {

    public void initialize(ValidItemsName constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {

        return !(value == null || value.isEmpty() || value.isBlank());
    }
}
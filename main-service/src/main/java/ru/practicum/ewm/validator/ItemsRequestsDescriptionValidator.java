package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemsRequestsDescriptionValidator implements ConstraintValidator<ValidItemsRequestsDescription, String> {

    public void initialize(ValidItemsRequestsDescription constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {

        return !(value == null || value.isEmpty() || value.isBlank());
    }
}
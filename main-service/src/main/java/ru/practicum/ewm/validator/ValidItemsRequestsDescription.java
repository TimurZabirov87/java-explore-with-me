package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemsRequestsDescriptionValidator.class)
@Documented
public @interface ValidItemsRequestsDescription {

    String message() default "Description is invalid: cannot be empty or blank";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

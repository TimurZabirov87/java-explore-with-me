package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CommentsTextValidator.class)
@Documented
public @interface ValidCommentsText {

    String message() default "Text is invalid: cannot be empty or blank";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

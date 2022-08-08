package com.bol.mancala.util.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PlayersMinSizeConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayersMinSizeConstraint {
    String message() default "The player set cannot contain less than 2 player.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

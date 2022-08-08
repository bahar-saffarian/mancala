package com.bol.mancala.util.annotation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class PlayersMinSizeConstraintValidator implements ConstraintValidator<PlayersMinSizeConstraint, Set<String>> {
    @Override
    public boolean isValid(Set<String> values, ConstraintValidatorContext context) {
        return values.size() >= 2;
    }
}

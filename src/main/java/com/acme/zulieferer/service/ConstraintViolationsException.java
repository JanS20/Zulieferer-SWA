package com.acme.zulieferer.service;

import com.acme.zulieferer.entity.Zulieferer;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
    private final transient Collection<ConstraintViolation<Zulieferer>> violations;

    ConstraintViolationsException(
        @SuppressWarnings("ParameterHidesMemberVariable")
        final Collection<ConstraintViolation<Zulieferer>> violations
    ) {
        super("Constraints sind verletzt");
        this.violations = violations;
    }
}

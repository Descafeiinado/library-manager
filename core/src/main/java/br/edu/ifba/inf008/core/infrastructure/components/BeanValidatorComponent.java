package br.edu.ifba.inf008.core.infrastructure.components;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class BeanValidatorComponent {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = validatorFactory.getValidator();

    public static void validateAndThrow(Object object) throws ConstraintViolationException {
        if (object == null) {
            throw new IllegalArgumentException("Object to validate cannot be null");
        }

        var violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}

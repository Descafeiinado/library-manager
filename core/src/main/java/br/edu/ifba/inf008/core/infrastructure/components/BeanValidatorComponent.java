package br.edu.ifba.inf008.core.infrastructure.components;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * BeanValidatorComponent is a utility class that provides methods to validate JavaBeans
 * using Jakarta Bean Validation (JSR 380).
 * It uses a Validator instance to perform validation and throws a ConstraintViolationException
 * if any validation constraints are violated.
 */
public class BeanValidatorComponent {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = validatorFactory.getValidator();

    /**
     * Validates the given object against its constraints.
     * If the object is null, an IllegalArgumentException is thrown.
     * If any validation constraints are violated, a ConstraintViolationException is thrown.
     *
     * @param object the object to validate
     * @throws ConstraintViolationException if validation constraints are violated
     * @throws IllegalArgumentException if the object to validate is null
     */
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

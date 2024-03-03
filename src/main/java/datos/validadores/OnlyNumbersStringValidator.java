package datos.validadores;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OnlyNumbersStringValidator implements ConstraintValidator <OnlyNumbers, String> {

    @Override
    public void initialize(OnlyNumbers constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            addConstraintViolation(context, "El valor no debe ser nulo ni estar vacío");
            return false;
        } else if (!value.matches("^[0-9]+$")) {
            addConstraintViolation(context, "Solo se aceptan números enteros positivos.");
            return false;
        } else if (value.length() > 10) {
            addConstraintViolation(context, "El valor no puede tener más de 10 dígitos");
            return false;
        } else if (Long.parseLong(value) < 1) {
            addConstraintViolation(context, "El valor debe ser igual o mayor que 1");
            return false;
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}

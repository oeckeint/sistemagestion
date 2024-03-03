package datos.validadores;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { OnlyNumbersStringValidator.class })
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyNumbers {
    String message() default "No se aceptan letras ni caracteres especiales. Solo se aceptan números enteros positivos.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


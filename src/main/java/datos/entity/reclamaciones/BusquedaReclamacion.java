package datos.entity.reclamaciones;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Component
public class BusquedaReclamacion {

    @Size(min = 1, message = "El valor no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Solo se aceptan valores alfanuméricos")
    private String valorActual;

    private String filtroActual;

    @Value("#{${reclamaciones.filtros}}")
    private LinkedHashMap<String, String> filtros;

}

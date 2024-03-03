package datos.entity.medidas;

import datos.validadores.OnlyNumbers;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MedidaValidatorForm {

    @OnlyNumbers
    private String valorSeleccionado;

    private String filtro;

    private List<String> filtros;

    public MedidaValidatorForm(){
        this.filtros = new ArrayList<>();
        this.filtros.add("QH");
    }

}

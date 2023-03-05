package controladores.helper.medidas;

import excepciones.MedidaTipoNoReconocido;
import org.springframework.stereotype.Component;

@Component
public class MedidasHelper {

    public TIPO_MEDIDA definirTipoMedida(String nombreArchivo) throws MedidaTipoNoReconocido {
        TIPO_MEDIDA t = TIPO_MEDIDA.DESCONOCIDO;
        String inicioArchivo = nombreArchivo.trim().substring(0,2).toLowerCase();
        switch (inicioArchivo){
            case "f5":
                t = TIPO_MEDIDA.F5;
                break;
            case "p5":
                t = TIPO_MEDIDA.P5;
                break;
            default:
                throw new MedidaTipoNoReconocido();
        }
        return t;
    }

    public enum TIPO_MEDIDA{
        F5, DESCONOCIDO, P5
    }

}

package controladores;

import datos.entity.Cliente;
import java.util.Arrays;
import java.util.List;

public class ClientesHelper {

    public static final String ICONO = "<i class='fas fa-users'></i>";
    public static final String TITULO_PAGINA = "Clientes";
    public static final String ENCABEZADO = ICONO + " " + TITULO_PAGINA;
    public static String ETIQUETA_TABLA_TITULO = "Registros";
    public static String SHOW_DELETE;

    public static String mensaje;
    public static Cliente cliente;

    public static final String INSTRUCCION_LISTAR = "Use la herramienta de búsqueda para encontrar fácilmente un registro.";
    public static final String INSTRUCCION_FORMULARIO = "Llene todos los campos y verifique que el CUPS sea correcto.";

    public static final String FORMULARIO_ERROR_CUPS = "Ops! Error en el CUPS, ";
    public static final String FORMULARIO_ERROR_CUPS_SIZE = FORMULARIO_ERROR_CUPS + "el tamaño debe de ser estar entre 20 y 22 caracteres (";
    public static final String FORMULARIO_ERROR_CUPS_ES = FORMULARIO_ERROR_CUPS + "debe comenzat con ES";
    public static final String FORMULARIO_ERROR_CUPS_FIN = FORMULARIO_ERROR_CUPS + " caracteres incorrectos ";
    public static final String FORMULARIO_ERROR_TARIFA = "La tarifa no es válida ";
    public static final String FORMULARIO_ERROR_CAMPOS = "Todos los campos son obligatorios.";

    public static final String FORMULARIO_EXITO_GUARDAR = "Se ha guardado exitosamente el cliente con el cups ";
    public static final String FORMULARIO_EXITO_ELIMINAR = "Se ha eliminado exitosamente el cliente con el cups ";

    public static final List<String> TARIFAS = Arrays.asList(
            "20A", "21A", "20DHA", "21DHA", "20TD",
            "30A", "31A", "30TD",
            "61A", "62A",
            "61TD", "62TD"
    );

    public static final boolean validarCliente(Cliente cliente) {
        if (!camposVacios(cliente) && tamañoCups(cliente.getCups()) && inicioCups(cliente.getCups()) && finCups(cliente.getCups()) && tarifaExiste(cliente.getTarifa())) {
            mensaje = FORMULARIO_EXITO_GUARDAR + cliente.getCups();
            return true;
        }
        return false;
    }

    public static final boolean camposVacios(Cliente cliente) {
        boolean valido = cliente.getCups().isEmpty() || cliente.getNombreCliente().isEmpty() || cliente.getTarifa().isEmpty();
        mensaje = valido ? FORMULARIO_ERROR_CAMPOS : "";
        return valido;
    }

    public static final boolean tamañoCups(String cups) {
        boolean valido = cups.length() > 19 && cups.length() < 23;
        mensaje = valido ? "" : FORMULARIO_ERROR_CUPS_SIZE + cups.length() + ")";
        return valido;
    }

    public static final boolean inicioCups(String cups) {
        boolean valido = cups.startsWith("ES");
        mensaje = valido ? "" : FORMULARIO_ERROR_CUPS_ES;
        return valido;
    }

    public static final boolean tarifaExiste(String tarifa) {
        boolean valido = TARIFAS.contains(tarifa);
        mensaje = valido ? "" : FORMULARIO_ERROR_TARIFA + tarifa;
        return valido;
    }

    public static final boolean finCups(String cups) {
        boolean valido;

        long valor = Long.parseLong(cups.substring(2, 18));
        String fin = cups.substring(18, 20);
        double aux = valor % 529;
        double parteDecimal = (aux / 23) % 1;
        double parteEntera = (aux / 23) - parteDecimal;

        valido = (fin.equals(letraCups(parteEntera) + letraCups(Math.round(parteDecimal * 23))));
        mensaje = valido ? "" : FORMULARIO_ERROR_CUPS_FIN + fin;

        return valido;
    }

    public static final String letraCups(double num) {
        String letraControl = "";
        switch ((int) num) {
            case 0:
                letraControl = "T";
                break;
            case 1:
                letraControl = "R";
                break;
            case 2:
                letraControl = "W";
                break;
            case 3:
                letraControl = "A";
                break;
            case 4:
                letraControl = "G";
                break;
            case 5:
                letraControl = "M";
                break;
            case 6:
                letraControl = "Y";
                break;
            case 7:
                letraControl = "F";
                break;
            case 8:
                letraControl = "P";
                break;
            case 9:
                letraControl = "D";
                break;
            case 10:
                letraControl = "X";
                break;
            case 11:
                letraControl = "B";
                break;
            case 12:
                letraControl = "N";
                break;
            case 13:
                letraControl = "J";
                break;
            case 14:
                letraControl = "Z";
                break;
            case 15:
                letraControl = "S";
                break;
            case 16:
                letraControl = "Q";
                break;
            case 17:
                letraControl = "V";
                break;
            case 18:
                letraControl = "H";
                break;
            case 19:
                letraControl = "L";
                break;
            case 20:
                letraControl = "C";
                break;
            case 21:
                letraControl = "K";
                break;
            case 22:
                letraControl = "E";
                break;
            default:
                letraControl = "0";
                break;
        }

        return letraControl;
    }

    public static void reiniciarVariables() {
        mensaje = null;
        cliente = null;
        SHOW_DELETE = null;
    }
}

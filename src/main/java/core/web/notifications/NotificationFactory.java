package core.web.notifications;

import excepciones.MasDeUnClienteEncontrado;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class NotificationFactory {

    public static NotificationProcess errorArchivo(
            String nombreArchivo, Exception ex) {

        return new NotificationProcess(
                NotificationProcess.NotificationType.ERROR,
                String.format(
                        "El archivo <strong>%s</strong> no se procesó: %s",
                        nombreArchivo,
                        ex.getMessage()
                )
        );
    }

    public static NotificationProcess masDeUnClienteEncontrado(String nombreArchivo, MasDeUnClienteEncontrado ex) {

        return new NotificationProcess(
                NotificationProcess.NotificationType.ERROR,
                "Se encontró más de un cliente para el archivo <strong>" + nombreArchivo + "</strong>: " + ex.getMessage()
        );
    }


    private static String mensaje(String nombreArchivo, Exception ex) {
        return String.format(
                "El archivo <strong>%s</strong> no se procesó: %s",
                nombreArchivo,
                ex.getMessage()
        );
    }
}

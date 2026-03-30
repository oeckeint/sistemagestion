package utileria;

import common.i18n.Messages;
import common.publisher.incident.publisher.model.CallerMethodResolveraa;
import common.publisher.incident.publisher.IncidentPublisherServiceBridge;
import common.publisher.incident.publisher.model.DocumentWarning;
import controladores.common.structure.ArchivoTextoMessageKey;
import controladores.helper.Utilidades;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ArchivoTexto {

    private static final Map<String, ArchivoTextoMessageKey> WARNING_KEYS_BY_CODE = buildWarningKeys();

    private enum PeajesLogFile {
        ERRORES("errores.txt"),
        ADVERTENCIAS("advertencias.txt");

        private final String fileName;

        PeajesLogFile(String fileName) {
            this.fileName = fileName;
        }

        public String fileName() {
            return fileName;
        }
    }

    public static void escribirError(String contenido) {
        String formateoContenido = limpiarEtiquetasResaltado(contenido);
        appendLineInPeajesFile(
                PeajesLogFile.ERRORES,
                formateoContenido,
                ArchivoTextoMessageKey.WRITE_ERROR_FAILED
        );
    }

    public static void escribirAdvertencia(
            String nombreArchivo,
            String codAdvertencia
    ) {
        String warningMessage = identificarAdvertencia(codAdvertencia);
        String warningLine = Messages.format(
                ArchivoTextoMessageKey.WARNING_LINE_WITH_FILE,
                warningMessage,
                nombreArchivo
        );
        appendLineInPeajesFile(
                PeajesLogFile.ADVERTENCIAS,
                warningLine,
                ArchivoTextoMessageKey.WRITE_WARNING_FAILED
        );
    }

    public static void publishWarning(DocumentWarning warning) {
        escribirAdvertencia(
                warning.getFileName(),
                warning.resolvedCode()
        );

        IncidentPublisherServiceBridge.publishDocumentWarning(warning);
    }


    //Utilileria
    private static String momentoActual() {
        return new SimpleDateFormat("<dd/MM/yyyy HH:mm:ss> ").format(new Date());
    }

    private static String identificarAdvertencia(String cod) {
        String safeCode = cod == null ? "" : cod;
        ArchivoTextoMessageKey warningKey = WARNING_KEYS_BY_CODE.getOrDefault(
                safeCode,
                ArchivoTextoMessageKey.WARNING_CODE_UNKNOWN
        );
        return Messages.format(
                ArchivoTextoMessageKey.WARNING_TEMPLATE,
                safeCode,
                Messages.get(warningKey)
        );
    }

    private static String limpiarEtiquetasResaltado(String contenido) {
        return contenido.replaceAll("<Strong>", "").replaceAll("</Strong>", "");
    }


    private static void appendLineInPeajesFile(PeajesLogFile logFile, String line, ArchivoTextoMessageKey errorKey) {
        String path = System.getProperty("user.dir") + "/resources/Peajes/";
        Utilidades.creacionDirectorios(path);
        File file = new File(path + logFile.fileName());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                if (file.length() == 0) {
                    bw.write(momentoActual() + line);
                } else {
                    bw.write("\n" + momentoActual() + line);
                }
            }
        } catch (IOException e) {
            System.out.println(Messages.get(errorKey));
        }
    }

    private static Map<String, ArchivoTextoMessageKey> buildWarningKeys() {
        Map<String, ArchivoTextoMessageKey> warningKeys = new HashMap<String, ArchivoTextoMessageKey>();
        warningKeys.put("1", ArchivoTextoMessageKey.WARNING_CODE_1);
        warningKeys.put("2", ArchivoTextoMessageKey.WARNING_CODE_2);
        warningKeys.put("3", ArchivoTextoMessageKey.WARNING_CODE_3);
        warningKeys.put("4", ArchivoTextoMessageKey.WARNING_CODE_4);
        warningKeys.put("5", ArchivoTextoMessageKey.WARNING_CODE_5);
        warningKeys.put("6", ArchivoTextoMessageKey.WARNING_CODE_6);
        warningKeys.put("7", ArchivoTextoMessageKey.WARNING_CODE_7);
        warningKeys.put("8", ArchivoTextoMessageKey.WARNING_CODE_8);
        warningKeys.put("9", ArchivoTextoMessageKey.WARNING_CODE_9);
        warningKeys.put("10", ArchivoTextoMessageKey.WARNING_CODE_10);
        warningKeys.put("11", ArchivoTextoMessageKey.WARNING_CODE_11);
        warningKeys.put("12", ArchivoTextoMessageKey.WARNING_CODE_12);
        warningKeys.put("13", ArchivoTextoMessageKey.WARNING_CODE_13);
        warningKeys.put("14", ArchivoTextoMessageKey.WARNING_CODE_14);
        warningKeys.put("15", ArchivoTextoMessageKey.WARNING_CODE_15);
        warningKeys.put("16", ArchivoTextoMessageKey.WARNING_CODE_16);
        warningKeys.put("17", ArchivoTextoMessageKey.WARNING_CODE_17);
        warningKeys.put("18", ArchivoTextoMessageKey.WARNING_CODE_18);
        warningKeys.put("19", ArchivoTextoMessageKey.WARNING_CODE_19);
        warningKeys.put("20", ArchivoTextoMessageKey.WARNING_CODE_20);
        warningKeys.put("21", ArchivoTextoMessageKey.WARNING_CODE_21);
        warningKeys.put("22", ArchivoTextoMessageKey.WARNING_CODE_22);
        warningKeys.put("23", ArchivoTextoMessageKey.WARNING_CODE_23);
        warningKeys.put("24", ArchivoTextoMessageKey.WARNING_CODE_24);
        warningKeys.put("25", ArchivoTextoMessageKey.WARNING_CODE_25);
        warningKeys.put("26", ArchivoTextoMessageKey.WARNING_CODE_26);
        warningKeys.put("27", ArchivoTextoMessageKey.WARNING_CODE_27);
        warningKeys.put("28", ArchivoTextoMessageKey.WARNING_CODE_28);
        warningKeys.put("29", ArchivoTextoMessageKey.WARNING_CODE_29);
        warningKeys.put("30", ArchivoTextoMessageKey.WARNING_CODE_30);
        warningKeys.put("31", ArchivoTextoMessageKey.WARNING_CODE_31);
        warningKeys.put("32", ArchivoTextoMessageKey.WARNING_CODE_32);
        return warningKeys;
    }
}


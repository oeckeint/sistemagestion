package core.controller;

import controladores.Procesamiento;
import controladores.helper.Etiquetas;
import controladores.helper.Utilidades;
import core.web.notifications.NotificationFactory;
import core.web.notifications.NotificationProcess;
import excepciones.ErrorDesconocidoException;
import excepciones.ExtensionArchivoNoReconocida;
import excepciones.MasDeUnClienteEncontrado;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utileria.ArchivoTexto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static core.web.notifications.NotificationProcess.NotificationType.ERROR;
import static core.web.notifications.NotificationProcess.NotificationType.WARNING;

@Controller
@RequestMapping("/procesar")
public class InvoiceImportController extends Procesamiento {

    @PostMapping("/importar")
    public String importation(@RequestParam("archivosxml") MultipartFile[] files, RedirectAttributes ra) throws IOException {
        List<NotificationProcess> notifications = new ArrayList<>();

        for (MultipartFile file : files) {
            this.reiniciarVariablesBooleanas();
            archivosTotales++;
            File f;
            FileOutputStream ous;
            String nombreArchivo = null;

            //Escritura en archivos temporales por cada archivo recibido
            try (InputStream fileContent = file.getInputStream()) {
                //Creación del los archivos temporales para su procesamiento
                f = File.createTempFile("processTemp", null);
                //File f = new File("C:\\a\\" + fileName);
                ous = new FileOutputStream(f);
                int dato = fileContent.read();
                while (dato != -1) {
                    ous.write(dato);
                    dato = fileContent.read();
                }

                //Fin del flujo de escritura
                ous.close();

                nombreArchivo = file.getOriginalFilename();
                switch (Utilidades.definirExtensionArchivo(nombreArchivo)){
                    case XML:
                        this.processInvoice(f, nombreArchivo).ifPresent(notifications::add);
                        break;
                    case MEDIDAS:
                        this.procesarMedidas(f, nombreArchivo);
                        break;
                    default:
                        throw new ExtensionArchivoNoReconocida();
                }

                //Eliminación del archivo temporal
                f.deleteOnExit();

            } catch (ExtensionArchivoNoReconocida e) {
                this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage());
                notifications.add(new NotificationProcess(
                        ERROR,
                        "El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + e.getMessage()
                ));
                ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
            } catch (MasDeUnClienteEncontrado e) {
                notifications.add(NotificationFactory.masDeUnClienteEncontrado(nombreArchivo, e));
            }

            catch (Exception e) {
                this.archivosErroneos.add("El archivo <Strong>" + nombreArchivo + "</Strong> no se proceso porque " + new ErrorDesconocidoException().getMessage() + " (<Strong>" + e.getMessage() + "</Strong>)");
                ArchivoTexto.escribirError(this.archivosErroneos.get(this.archivosErroneos.size() - 1));
                e.printStackTrace(System.out);
            }
        }
        ra.addFlashAttribute("notifications", notifications);
        Etiquetas.PROCESAMIENTO_FORMULARIO_MENSAJE = "Archivos Procesados (" + archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/procesar";
    }

    public Optional<NotificationProcess> processInvoice(File f, String nombre) {
        try {
            procesarXML(f, nombre);
            return Optional.empty();
        } catch (MasDeUnClienteEncontrado e) {
            return Optional.of(
                    new NotificationProcess(ERROR, mensaje(nombre, e))
            );
        }
    }


    private String mensaje(String nombreArchivo, Exception ex) {
        return String.format(
                "El archivo <strong>%s</strong> no se procesó: %s",
                nombreArchivo,
                ex.getMessage()
        );
    }

}

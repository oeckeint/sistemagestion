package controladores;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import controladores.helper.Etiquetas;
import excepciones.CredencialesIncorrectasException;
import excepciones.ErrorAlConectarConElServidorException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import utileria.StringHelper;

@Controller
@RequestMapping("/ftp")
@PropertySource("classpath:ftp.properties")
public class FTP {
	
	@Autowired
	private Environment env;

    private String host;
    private int port;
    private String user;
    private String password;
    private FTPClient clienteFTP;
    private int archivosCorrectos;
    private int archivosTotales;

    private String dir;
    private JSch jsch;
    private Session session;
    private ChannelSftp channelSftp;

    @GetMapping("")
    public String formulario(Model model) {
        model.addAttribute("tituloPagina", Etiquetas.FTP_FORMULARIO_TITULO_PAGINA);
        model.addAttribute("titulo", Etiquetas.FTP_FORMULARIO_ENCABEZADO);
        model.addAttribute("mensajeRegistro", Etiquetas.FTP_FORMULARIO_MENSAJE);
        model.addAttribute("etiquetaBoton", Etiquetas.FTP_FORMULARIO_ETIQUETA_BOTON);
        this.reiniciarVariables();
        return "comunes/formulario_sftp";
    }

    /**
     * For testing use filezilla server
     * Directory where everything is stored is C:\Peajes\ftp\*user1*\httpdocs 
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/subir", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String subirArchivos(@RequestParam("archivos") MultipartFile[] files) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileInputStream fs = null;
        File f = null;
        for (MultipartFile file : files) {
            archivosTotales++;
            try (InputStream fileContent = file.getInputStream()) {
                f = File.createTempFile("pdfTemp", null);
                fos = new FileOutputStream(f);
                int dato;
                while ((dato = fileContent.read()) != -1) {
                    fos.write(dato);
                }

                fs = new FileInputStream(f);
                if (this.cargarConfiguraciones()) {
                	this.clienteFTP.setFileType(FTPClient.BINARY_FILE_TYPE);
                    //this.clienteFTP.enterRemotePassiveMode();
                    //this.clienteFTP.changeWorkingDirectory(".\\httpdocs\\" + this.definirCarpeta(file.getOriginalFilename()));
                    this.clienteFTP.storeFile("httpdocs/" + this.definirCarpeta(file.getOriginalFilename()) + "/" + file.getOriginalFilename(), fs);
                    fs.close();
                    //this.clienteFTP.rename(file.getOriginalFilename(), "httpdocs/" + file.getOriginalFilename());

                    this.clienteFTP.logout();
                    this.clienteFTP.disconnect();
                    archivosCorrectos++;
				} else {
					System.out.println("No se pudieron cargar los datos de ftp");
				}
            } catch (Exception e) {
                System.out.println(e.getClass());
                e.printStackTrace(System.out);
            } finally {
                IOUtils.closeQuietly(fos);
                //IOUtils.close(fs);
                if (f != null) {
                    f.deleteOnExit();
                }
            }
        }
        Etiquetas.FTP_FORMULARIO_MENSAJE = "Archivos Subidos (" + archivosCorrectos + " de " + archivosTotales + ")";
        return "redirect:/ftp";
    }

    private String definirCarpeta(String nombreArchivo) {
        if (!StringHelper.isValid(nombreArchivo) || nombreArchivo.length() < 11 || !nombreArchivo.contains("ES")) {
            return "";
        }
        String codigo = nombreArchivo.substring(7, 11);
        String carpeta = "";
        switch (codigo) {
            case "0021":
                carpeta = "iberdrola";
                break;
            case "0022":
                carpeta = "Naturgy";
                break;
            case "0023":
            case "0024":
            case "0031":
                carpeta = "docendesa";
                break;
            case "0032":
                carpeta = "ElecConquense";
                break;
            case "0113":
                carpeta = "Estebanell";
                break;
            case "0117":
                carpeta = "ElectraCaldense";
                break;
            case "0118":
                carpeta = "AnselmoLeon";
                break;
            default:
                break;
        }
        return carpeta;
    }

    public void listarArchivos(String directorio) {
        try {
            cargarConfiguraciones();
            this.session.connect();
            this.channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            this.channelSftp.connect();

            Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(directorio);
            System.out.println("Archivos en el directorio " + directorio);
            for (ChannelSftp.LsEntry entry : entries) {
                System.out.println(entry.getFilename());
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            this.channelSftp.disconnect();
            this.session.disconnect();
        }
    }

    public void descargarArchivo(String pathArchivo, String nombreArchivo) {
        InputStream is = null;
        FileWriter fw = null;
        try {
            cargarConfiguraciones();
            this.session.connect();
            this.channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            this.channelSftp.connect();

            this.channelSftp.cd("/");
            is = this.channelSftp.get(pathArchivo + "/" + nombreArchivo);
            byte[] bytes = IOUtils.toByteArray(is);
            File f = new File("C:\\Peajes\\ftp\\" + nombreArchivo);
            fw = new FileWriter(f);
            fw.write(new String(bytes));

        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            this.channelSftp.disconnect();
            this.session.disconnect();
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fw);
        }

    }

    public void eliminarArchivo(String path, String nombreArchivo) {
        try {
            cargarConfiguraciones();
            this.session.connect();
            this.channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            this.channelSftp.connect();
            channelSftp.rm(path + "/" + nombreArchivo);
        } catch (Exception e) {
            String mensajeError = e.getMessage();
            if (mensajeError.toLowerCase().contains("file not found")) {
                System.out.println("El archivo no existe en el path especificado");
            } else {
                e.printStackTrace(System.out);
            }
        } finally {
            this.channelSftp.disconnect();
            this.session.disconnect();
        }
    }

    public boolean cargarConfiguraciones() throws ErrorAlConectarConElServidorException, CredencialesIncorrectasException, FileNotFoundException, IOException {
        FileInputStream fis = null;
        boolean completado = false;
		try (InputStream inputStream = getClass().getResourceAsStream("/ftp.properties");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
		    String contents = reader.lines()
		      .collect(Collectors.joining(System.lineSeparator()));
            this.dir = "C:\\";
            Properties prop = new Properties();
            //String propFile = "/ftp.properties";
            InputStream inputStreamRef = new ByteArrayInputStream(contents.getBytes(Charset.forName("UTF-8")));
            //fis = new FileInputStream(propFile);
            prop.load(inputStreamRef);
            this.host = prop.getProperty("sftp.host");
            this.port = Integer.parseInt(prop.getProperty("sftp.port"));
            this.user = prop.getProperty("sftp.user");
            this.password = prop.getProperty("sftp.password");
            this.clienteFTP = new FTPClient();
            this.clienteFTP.connect(this.host, this.port);
            int respuestaServer = clienteFTP.getReplyCode();
            if (!FTPReply.isPositiveCompletion(respuestaServer)) {
                throw new ErrorAlConectarConElServidorException(this.host, respuestaServer);
            }
            if (!clienteFTP.login(this.user, this.password)) {
                throw new CredencialesIncorrectasException(this.user);
            }
            completado = true;
        } finally {
            IOUtils.closeQuietly(fis);
        }
		return completado;
    }

    private void reiniciarVariables() {
        Etiquetas.FTP_FORMULARIO_MENSAJE = "Archivos para subir al servidor FTP, dependiendo del tamaño de los archivos la acción podría tardar un tiempo.";
        this.archivosCorrectos = 0;
        this.archivosTotales = 0;
    }

}

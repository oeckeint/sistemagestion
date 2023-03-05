package controladores.helper.medidas;

import datos.entity.Cliente;
import datos.entity.Medida;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.MedidaTipoNoReconocido;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import utileria.ArchivoTexto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ProcesarMedida {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final CrudDao<Medida> medidasService;
    protected ClienteService clienteService;

    public ProcesarMedida(@Qualifier(value = "medidaServiceImp") CrudDao<Medida> medidasService, ClienteService clienteService) {
        this.medidasService = medidasService;
        this.clienteService = clienteService;
    }

    /**
     * Lee todas las lineas que contiene el archivo y las guarda en la base de datos en la tabla de medidas
     * @param file
     * @param nombreArchivo
     * @throws MedidaTipoNoReconocido
     */
    public void guardar(File file, String nombreArchivo) {
        this.obtenerMedidasArchivo(file, nombreArchivo).forEach(medidasService::guardar);
    }

    /**
     * Extraer cada una de las lineas y crea un objeto del tipo Medida asignandolo a una lista de Medidas
     * @param f
     * @param nombreArchivo
     * @return
     */
    private List<Medida> obtenerMedidasArchivo(File f, String nombreArchivo){
        List<Medida> medidas = new ArrayList<>();
        Medida medida;
        FileReader fr = null;
        BufferedReader br = null;
        String[] elementos = null;
        int lineaActual = 1;

        try {
            fr = new FileReader (f);
            br = new BufferedReader(fr);

            String linea;

            while((linea = br.readLine()) != null){
                elementos = linea.split(";");

                if (elementos.length == 12) {
                    Cliente cliente = clienteService.encontrarCups(elementos[0]);
                    if (cliente != null) {
                        if (!cliente.getTarifa().equals("20TD")){
                            medida = new Medida();
                            medida.setCliente(cliente);
                            medida.setFecha(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(elementos[1]));
                            medida.setBanderaInvVer(Integer.parseInt(elementos[2]));
                            medida.setAe1(Integer.parseInt(elementos[3]));
                            medida.setAs1(Integer.parseInt(elementos[4]));
                            medida.setRq1(Integer.parseInt(elementos[5]));
                            medida.setRq2(Integer.parseInt(elementos[6]));
                            medida.setRq3(Integer.parseInt(elementos[7]));
                            medida.setRq4(Integer.parseInt(elementos[8]));
                            medida.setMetodObj(Integer.parseInt(elementos[9]));
                            medida.setIndicFirmez(Integer.parseInt(elementos[10]));
                            medida.setCodigoFactura(elementos[11]);
                            medidas.add(medida);
                        } else {
                            logger.log(Level.INFO, ">>> El cliente con el cups {0}, tiene tarifa 20TD. No se guarda en la DB", elementos[0]);
                            ArchivoTexto.escribirError("El cliente con el cups " + elementos[0] + ", tiene tarifa 20TD. No se guarda en la DB del archivo " + nombreArchivo);
                        }
                    } else {
                        logger.log(Level.INFO, ">>> No existe el cliente con el cups {0}", elementos[0]);
                        ArchivoTexto.escribirError("No se encontró un cliente con el cups " + elementos[0] + " en el archivo " + nombreArchivo);
                    }
                } else {
                    logger.log(Level.INFO, ">>> No tiene suficientes datos especificados {0} en archivo {1} en la linea {2}", new Object[]{ Arrays.toString(elementos), nombreArchivo, lineaActual});
                    ArchivoTexto.escribirError("No tiene suficientes datos especificados " +  Arrays.toString(elementos) + " en la linea " + lineaActual + " del archivo " + nombreArchivo);
                }
                lineaActual++;
            }
        } catch (MasDeUnClienteEncontrado e){
            logger.log(Level.INFO, ">>> Se encontró mas de un cliente con el cups {0}", elementos[0]);
            ArchivoTexto.escribirError("Se encontró mas de un cliente con el cups " + elementos[0] + " en la linea " + lineaActual + " del archivo " + nombreArchivo);
        } catch(Exception e){
            logger.log(Level.INFO, ">>> Error desconocido al procesar {0}", Arrays.toString(elementos));
            e.printStackTrace();
            ArchivoTexto.escribirError("Error desconocido con los elementos " + Arrays.toString(elementos) + " en la linea " + lineaActual + " del archivo " + nombreArchivo);
        }finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            } catch (Exception e2) {e2.printStackTrace();}
        }
        return medidas;
    }

}

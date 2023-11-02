package controladores.helper.medidas;

import datos.entity.Cliente;
import datos.entity.medidas.MedidaCCH;
import datos.entity.medidas.MedidaQH;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import excepciones.MasDeUnClienteEncontrado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import utileria.ArchivoTexto;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ProcesarMedidaQH {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final ClienteService clienteService;
    private final CrudDao<MedidaQH> medidasQHService;

    public ProcesarMedidaQH(@Qualifier(value = "medidaQHServiceImp") CrudDao<MedidaQH> medidasQHService, ClienteService clienteService) {
        this.medidasQHService = medidasQHService;
        this.clienteService = clienteService;
    }
    public void guardar(File file, String nombreArchivo){
        this.obtenerMedidasArchivo(file, nombreArchivo).forEach(medidasQHService::guardar);
    }
    private List<MedidaQH> obtenerMedidasArchivo(File file, String nombreArchivo){
        List<MedidaQH> medidas = new ArrayList<>();
        MedidaQH medida;
        FileReader fr = null;
        BufferedReader br = null;
        String[] elementos = null;
        int lineaActual = 1;

        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);

            String linea;

            while ((linea = br.readLine()) != null){
                elementos = linea.split(";");
                if(elementos.length > 20 && elementos.length < 23){
                    Cliente cliente = clienteService.encontrarCups(elementos[0]);
                    if(cliente != null){
                        medida = new MedidaQH();
                        medida.setCliente(cliente);
                        medida.setTipoMed(Integer.parseInt(elementos[1]));
                        medida.setFecha(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(elementos[2]));
                        medida.setBanderaInvVer(Integer.parseInt(elementos[3]));
                        medida.setActent(Integer.parseInt(elementos[4]));
                        medida.setQactent(Integer.parseInt(elementos[5]));
                        medida.setActsal(Integer.parseInt(elementos[6]));
                        medida.setQactsal(Integer.parseInt(elementos[7]));
                        medida.setR_q1(Integer.parseInt(elementos[8]));
                        medida.setQr_q1(Integer.parseInt(elementos[9]));
                        medida.setR_q2(Integer.parseInt(elementos[10]));
                        medida.setQr_q2(Integer.parseInt(elementos[11]));
                        medida.setR_q3(Integer.parseInt(elementos[12]));
                        medida.setQr_q3(Integer.parseInt(elementos[13]));
                        medida.setR_q4(Integer.parseInt(elementos[14]));
                        medida.setQr_q4(Integer.parseInt(elementos[15]));
                        medida.setMedres1(Integer.parseInt(elementos[16]));
                        medida.setQmedres1(Integer.parseInt(elementos[17]));
                        medida.setMedres2(Integer.parseInt(elementos[18]));
                        medida.setQmedres2(Integer.parseInt(elementos[19]));
                        medida.setMetodObt(Integer.parseInt(elementos[20]));
                        //Se verifica si la entrada de datos contiene más de 21 datos
                        if (elementos.length > 21){
                            medida.setTemporal(Integer.parseInt(elementos[21]));
                        } else {
                            logger.log(Level.INFO, ">>> No se encontré el valor en la posición 22 de los datos {0}, del archivo {1} se rellena con un valor default {2}", new Object[]{ Arrays.toString(elementos), nombreArchivo, 99});
                            medida.setTemporal(99);
                        }
                        medidas.add(medida);
                    } else{
                        logger.log(Level.INFO, ">>> No existe el cliente con el cups {0}", elementos[0]);
                        ArchivoTexto.escribirError("No se encontró un cliente con el cups " + elementos[0] + " en el archivo " + nombreArchivo);
                    }
                } else {
                    logger.log(Level.INFO, ">>> No tiene suficientes datos especificados {0} en archivo {1} en la linea {2}", new Object[]{ Arrays.toString(elementos), nombreArchivo, lineaActual});
                    ArchivoTexto.escribirError("La entrada " +  Arrays.toString(elementos) + " en la linea " + lineaActual + " del archivo " + nombreArchivo + " no coincide con la cantidad de datos esperados");
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

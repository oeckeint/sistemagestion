package controladores.helper.medidas;

import datos.entity.Cliente;
import datos.entity.medidas.MedidaH;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import excepciones.MasDeUnClienteEncontrado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class MedidaHHandler {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final CrudDao<MedidaH> medidaHService;
    protected ClienteService clienteService;
    private final MedidasHelper medidasHelper;

    public MedidaHHandler(@Qualifier(value = "medidaHServiceImp") CrudDao<MedidaH> medidaHService, ClienteService clienteService, MedidasHelper medidasHelper){
        this.medidaHService = medidaHService;
        this.clienteService = clienteService;
        this.medidasHelper = medidasHelper;
    }

    public void procesarMedidasDesdeArchivo(File file, String nombreArchivo){
        int numeroHilos = 5;
        String usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
        ExecutorService executor = Executors.newFixedThreadPool(numeroHilos);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String contenidoDelinea;
            int numeroDeLinea = 1;
            while ((contenidoDelinea = br.readLine()) != null) {
                Runnable tarea = new TareaProcesamiento(numeroDeLinea, contenidoDelinea, nombreArchivo, usuarioActual);
                executor.execute(tarea);
                numeroDeLinea++;
            }

            executor.shutdown();
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                System.out.println("Se termino el archivo");
            } else {
                System.out.println("El archivo está siendo procesado en segundo plano");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace(System.out);
            logger.severe("Ha ocurrido un error al leer el archivo " + nombreArchivo);
        }
    }

    private class TareaProcesamiento implements Runnable {

        private final int numeroDeLinea;
        private final String contenidoDeLinea;
        private final String nombreArchivo;
        private final String usuario;
        private String cups;

        public TareaProcesamiento(int numeroDeLinea, String contenidoDeLinea, String nombreArchivo, String usuario) {
            this.numeroDeLinea = numeroDeLinea;
            this.contenidoDeLinea = contenidoDeLinea;
            this.nombreArchivo = nombreArchivo;
            this.usuario = usuario;
        }

        @Override
        public void run() {
            try {
                System.out.println("#" + numeroDeLinea);
                String[] elementos = contenidoDeLinea.split(";");
                if (elementos.length == 22) {
                    this.cups = elementos[0];
                    Cliente cliente = clienteService.encontrarCups(cups);
                    if (cliente != null) {
                        MedidaH medida = new MedidaH();
                        medida.setCliente(cliente);
                        medida.setTipoMedida(Integer.parseInt(elementos[1]));
                        medida.setFecha(medidasHelper.parsearFecha(elementos[2]));
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
                        medida.setTemporal(Integer.parseInt(elementos[21]));
                        medida.setCreatedOn(Calendar.getInstance());
                        medida.setCreatedBy(this.usuario);
                        medidaHService.guardar(medida);

                    } else {
                        medidasHelper.manejarClienteNoEncontrado(cups, nombreArchivo, numeroDeLinea);
                    }
                } else {
                    medidasHelper.manejarDatosInvalidos(elementos, nombreArchivo, numeroDeLinea);
                }
            } catch (MasDeUnClienteEncontrado e) {
                medidasHelper.manejarMasDeUnClienteEncontrado(cups, nombreArchivo, numeroDeLinea);
                e.printStackTrace(System.out);
            } catch (Exception e) {
                medidasHelper.manejarErrorDesconocido(e, nombreArchivo, numeroDeLinea);
            }
        }
    }

}

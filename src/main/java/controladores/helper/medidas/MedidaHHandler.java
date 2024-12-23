package controladores.helper.medidas;

import datos.entity.Cliente;
import datos.entity.medidas.MedidaH;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import datos.service.medidas.MedidaHServiceImp;
import excepciones.MasDeUnClienteEncontrado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Slf4j
@Component
public class MedidaHHandler {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final MedidaHServiceImp medidaHService;
    private final ClienteService clienteService;
    private final MedidasHelper medidasHelper;
    private final Set<Integer> lineasProcesadas = ConcurrentHashMap.newKeySet();

    @Autowired
    public MedidaHHandler(MedidaHServiceImp medidaHService, ClienteService clienteService, MedidasHelper medidasHelper) {
        this.medidaHService = medidaHService;
        this.clienteService = clienteService;
        this.medidasHelper = medidasHelper;
    }

    public Queue<String> procesarMedidasDesdeArchivo(File file, String nombreArchivo) {
        medidasHelper.clearErrores();

        if (!medidaHService.existeOrigen(nombreArchivo)) {
            medidasHelper.addError("El archivo " + nombreArchivo + " ya ha sido procesado");
            return new ConcurrentLinkedQueue<>(this.medidasHelper.errores);
        }

        int numeroHilos = Math.min(Runtime.getRuntime().availableProcessors(), 10);
        String usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
        ExecutorService executor = Executors.newFixedThreadPool(numeroHilos);
        int totalLineas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String contenidoDelinea;
            int numeroDeLinea = 1;

            while ((contenidoDelinea = br.readLine()) != null) {
                totalLineas++;
                Runnable tarea = new TareaProcesamiento(numeroDeLinea, contenidoDelinea, nombreArchivo, usuarioActual);
                executor.execute(tarea);
                numeroDeLinea++;
            }

            executor.shutdown();
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                logger.info("Se terminó de procesar el archivo " + nombreArchivo);
            } else {
                logger.warning("El archivo " + nombreArchivo + " está siendo procesado en segundo plano");
            }

            verificarLineasProcesadas(totalLineas, nombreArchivo);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("El procesamiento del archivo " + nombreArchivo + " fue interrumpido");
        } catch (IOException e) {
            logger.severe("Ha ocurrido un error al leer el archivo " + nombreArchivo);
            e.printStackTrace(System.out);
        }

        lineasProcesadas.clear();
        return new ConcurrentLinkedQueue<>(this.medidasHelper.errores);
    }

    private void verificarLineasProcesadas(int totalLineas, String nombreArchivo) {
        if (lineasProcesadas.size() < totalLineas) {
            int lineasFaltantes = totalLineas - lineasProcesadas.size();
            logger.severe(String.format("No todas las líneas fueron procesadas. Faltan %d líneas en el archivo %s", lineasFaltantes, nombreArchivo));
        } else {
            logger.info(String.format("Todas las líneas del archivo %s fueron procesadas exitosamente", nombreArchivo));
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
            if (lineasProcesadas.add(numeroDeLinea)) {
                String[] elementos = contenidoDeLinea.split(";");
                int elementoActual = 0;
                try {
                    logger.info(String.format("Procesando línea %d del archivo %s", numeroDeLinea, nombreArchivo));
                    if (elementos.length == 22) {
                        this.cups = elementos[elementoActual];
                        Cliente cliente = clienteService.encontrarCups(cups);
                        if (cliente != null) {
                            MedidaH medida = new MedidaH();
                            medida.setCliente(cliente);
                            medida.setTipoMedida(Integer.parseInt(elementos[++elementoActual]));
                            medida.setFecha(medidasHelper.parsearFecha(elementos[++elementoActual]));
                            medida.setBanderaInvVer(Float.parseFloat(elementos[++elementoActual]));
                            medida.setActent(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQactent(Float.parseFloat(elementos[++elementoActual]));
                            medida.setActsal(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQactsal(Float.parseFloat(elementos[++elementoActual]));
                            medida.setR_q1(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQr_q1(Float.parseFloat(elementos[++elementoActual]));
                            medida.setR_q2(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQr_q2(Float.parseFloat(elementos[++elementoActual]));
                            medida.setR_q3(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQr_q3(Float.parseFloat(elementos[++elementoActual]));
                            medida.setR_q4(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQr_q4(Float.parseFloat(elementos[++elementoActual]));
                            medida.setMedres1(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQmedres1(Float.parseFloat(elementos[++elementoActual]));
                            medida.setMedres2(Float.parseFloat(elementos[++elementoActual]));
                            medida.setQmedres2(Float.parseFloat(elementos[++elementoActual]));
                            medida.setMetodObt(Integer.parseInt(elementos[++elementoActual]));
                            medida.setTemporal(Integer.parseInt(elementos[++elementoActual]));
                            medida.setOrigen(this.nombreArchivo);
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
                    lineasProcesadas.remove(numeroDeLinea); // Permitir reintento
                } catch (NumberFormatException e) {
                    medidasHelper.manejarErrorDeConversion(elementos[elementoActual], nombreArchivo, numeroDeLinea);
                    lineasProcesadas.remove(numeroDeLinea); // Permitir reintento
                } catch (Exception e) {
                    medidasHelper.manejarErrorDesconocido(e, nombreArchivo, numeroDeLinea);
                    lineasProcesadas.remove(numeroDeLinea); // Permitir reintento
                }
            } else {
                logger.warning(String.format("La línea %d ya está siendo procesada", numeroDeLinea));
            }
        }
    }
}

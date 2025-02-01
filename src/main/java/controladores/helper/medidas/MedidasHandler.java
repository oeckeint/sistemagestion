package controladores.helper.medidas;

import datos.entity.Cliente;
import datos.entity.medidas.MedidaH;
import datos.entity.medidas.MedidaQH;
import datos.interfaces.ClienteService;
import datos.service.medidas.MedidaHService;
import datos.service.medidas.MedidaQHService;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.MedidaTipoNoReconocido;
import excepciones.medidas.NombreArchivoContieneEspacios;
import excepciones.medidas.NombreArchivoElementosTamanoDiferente;
import excepciones.medidas.NombreArchivoSinExtension;
import excepciones.medidas.NombreArchivoTamanoDiferente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
@Component
public class MedidasHandler {
    
    private final MedidaHService medidaHService;
    private final MedidaQHService medidaQHService;
    private final ClienteService clienteService;
    private final MedidasHelper medidasHelper;
    private final Set<Integer> lineasProcesadas = ConcurrentHashMap.newKeySet();

    public MedidasHandler(MedidaHService medidaHService, MedidaQHService medidaQHService, ClienteService clienteService, MedidasHelper medidasHelper) {
        this.medidaHService = medidaHService;
        this.medidaQHService = medidaQHService;
        this.clienteService = clienteService;
        this.medidasHelper = medidasHelper;
    }

    public Queue<String> procesarMedida(File file, String nombreArchivo, MedidasHelper.TIPO_MEDIDA tipoMedida) throws MedidaTipoNoReconocido, NombreArchivoTamanoDiferente, NombreArchivoContieneEspacios, NombreArchivoElementosTamanoDiferente, NombreArchivoSinExtension {
        medidasHelper.clearErrores();

        if (this.archivoYaProcesado(nombreArchivo, tipoMedida)) {
            medidasHelper.addError("El archivo " + nombreArchivo + " ya ha sido procesado.");
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
                Runnable tarea = new ProcesamientoMedidas(numeroDeLinea, contenidoDelinea, nombreArchivo, usuarioActual, tipoMedida);
                executor.execute(tarea);
                numeroDeLinea++;
            }

            executor.shutdown();
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS))
                log.warn("El archivo {} está siendo procesado en segundo plano", nombreArchivo);

            verificarLineasProcesadas(totalLineas, nombreArchivo);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("El procesamiento del archivo {} fue interrumpido", nombreArchivo);
        } catch (IOException e) {
            log.error("Ha ocurrido un error al leer el archivo {}", nombreArchivo);
        }

        lineasProcesadas.clear();
        return new ConcurrentLinkedQueue<>(this.medidasHelper.errores);
    }

    private void verificarLineasProcesadas(int totalLineas, String nombreArchivo) {
        if (lineasProcesadas.size() < totalLineas) {
            int lineasFaltantes = totalLineas - lineasProcesadas.size();
            log.error("No todas las líneas fueron procesadas. Faltan {} líneas en el archivo {}", lineasFaltantes, nombreArchivo);
        } else {
            log.info("Todas las líneas del archivo {} fueron procesadas exitosamente", nombreArchivo);
        }
    }

    private boolean archivoYaProcesado(String nombreArchivo, MedidasHelper.TIPO_MEDIDA tipoMedida) throws MedidaTipoNoReconocido {
        switch (tipoMedida) {
            case P1:
                return this.medidaHService.existeOrigen(nombreArchivo);
            case P2:
                return this.medidaQHService.existeOrigen(nombreArchivo);
            default:
                throw new MedidaTipoNoReconocido();
        }

    }

    private class ProcesamientoMedidas implements Runnable {

        private final int numeroDeLinea;
        private final String contenidoDeLinea;
        private final String nombreArchivo;
        private final String usuario;
        private final MedidasHelper.TIPO_MEDIDA tipoMedida;

        public ProcesamientoMedidas(int numeroDeLinea, String contenidoDeLinea, String nombreArchivo, String usuario, MedidasHelper.TIPO_MEDIDA tipoMedida) {
            this.numeroDeLinea = numeroDeLinea;
            this.contenidoDeLinea = contenidoDeLinea;
            this.nombreArchivo = nombreArchivo;
            this.usuario = usuario;
            this.tipoMedida = tipoMedida;
        }

        @Override
        public void run() {
            if (lineasProcesadas.add(numeroDeLinea)) {
                String[] elementos = contenidoDeLinea.split(";");
                String cups = elementos[0];
                try {
                    Cliente cliente = clienteService.encontrarCups(cups);
                    if (cliente == null) {
                        medidasHelper.manejarClienteNoEncontrado(cups, nombreArchivo, numeroDeLinea);
                        return;
                    }
                    switch (this.tipoMedida){
                        case P1:
                            this.procesarMedidaH(elementos, cliente);
                            break;
                        case P2:
                            this.procesarMedidaQH(elementos, cliente);
                            break;
                        default:
                            break;
                    }
                } catch (MasDeUnClienteEncontrado e) {
                    medidasHelper.manejarMasDeUnClienteEncontrado(cups, nombreArchivo, numeroDeLinea);
                } catch (Exception e) {
                    medidasHelper.manejarErrorDesconocido(e, nombreArchivo, numeroDeLinea);
                }
            }
        }

        private void procesarMedidaH(String[] elementos, Cliente cliente) {
            if (elementos.length != 22) {
                medidasHelper.manejarDatosInvalidos(elementos, nombreArchivo, numeroDeLinea, 22);
                return;
            }

            int elementoActual = 0;
            try {
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
            } catch (NumberFormatException e) {
                medidasHelper.manejarErrorDeConversion(elementos[elementoActual], nombreArchivo, numeroDeLinea);
            }

        }

        private void procesarMedidaQH(String[] elementos, Cliente cliente) {
            if (elementos.length < 21 || elementos.length > 22) {
                medidasHelper.manejarDatosInvalidos(elementos, nombreArchivo, numeroDeLinea, 21);
                return;
            }

            int elementoActual = 0;
            try {
                MedidaQH medida = new MedidaQH();
                medida.setCliente(cliente);
                medida.setTipoMed(Integer.parseInt(elementos[++elementoActual]));
                medida.setFecha(medidasHelper.parsearFecha(elementos[++elementoActual]));
                medida.setBanderaInvVer(Integer.parseInt(elementos[++elementoActual]));
                medida.setActent(Integer.parseInt(elementos[++elementoActual]));
                medida.setQactent(Integer.parseInt(elementos[++elementoActual]));
                medida.setActsal(Integer.parseInt(elementos[++elementoActual]));
                medida.setQactsal(Integer.parseInt(elementos[++elementoActual]));
                medida.setR_q1(Integer.parseInt(elementos[++elementoActual]));
                medida.setQr_q1(Integer.parseInt(elementos[++elementoActual]));
                medida.setR_q2(Integer.parseInt(elementos[++elementoActual]));
                medida.setQr_q2(Integer.parseInt(elementos[++elementoActual]));
                medida.setR_q3(Integer.parseInt(elementos[++elementoActual]));
                medida.setQr_q3(Integer.parseInt(elementos[++elementoActual]));
                medida.setR_q4(Integer.parseInt(elementos[++elementoActual]));
                medida.setQr_q4(Integer.parseInt(elementos[++elementoActual]));
                medida.setMedres1(Integer.parseInt(elementos[++elementoActual]));
                medida.setQmedres1(Integer.parseInt(elementos[++elementoActual]));
                medida.setMedres2(Integer.parseInt(elementos[++elementoActual]));
                medida.setQmedres2(Integer.parseInt(elementos[++elementoActual]));
                medida.setMetodObt(Integer.parseInt(elementos[++elementoActual]));
                medida.setTemporal(elementos.length > ++elementoActual ? Integer.parseInt(elementos[elementoActual]) : 99);
                medida.setOrigen(this.nombreArchivo);
                medida.setCreatedBy(this.usuario);
                medidaQHService.guardar(medida);
            } catch (NumberFormatException e) {
                medidasHelper.manejarErrorDeConversion(elementos[elementoActual], nombreArchivo, numeroDeLinea);
            }
        }

    }

}

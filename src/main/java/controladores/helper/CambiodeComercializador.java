package controladores.helper;

import datos.entity.Cliente;
import datos.entity.ClienteContrato;
import datos.entity.ClientePuntoSuministro;
import datos.entity.Tarifa;
import datos.interfaces.ClienteService;
import datos.interfaces.CrudDao;
import excepciones.ArchivoNoCumpleParaSerClasificado;
import excepciones.ClienteNoExisteException;
import excepciones.MasDatosdeLosEsperadosException;
import excepciones.MasDeUnClienteEncontrado;
import excepciones.TarifaNoExisteException;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utileria.StringHelper;

public class CambiodeComercializador extends xmlHelper {

    private final CrudDao tarifasService;

    /**
     * Constructor para los datos de cambioComercializador
     *
     * @param doc
     * @param clienteService
     * @param tarifasService
     * @throws MasDeUnClienteEncontrado
     * @throws ClienteNoExisteException
     * @throws ArchivoNoCumpleParaSerClasificado
     * @throws MasDatosdeLosEsperadosException
     * @throws excepciones.TarifaNoExisteException
     */
    public CambiodeComercializador(Document doc, ClienteService clienteService, CrudDao tarifasService) throws MasDeUnClienteEncontrado, ClienteNoExisteException, ArchivoNoCumpleParaSerClasificado, MasDatosdeLosEsperadosException, TarifaNoExisteException {
        super(doc, clienteService);
        this.tarifasService = tarifasService;
        this.procesar();
    }

    /**
     * Primero obtiene todos los datos y los agrega al objeto de cliente en caso
     * de que todo salga bien se guarda el registro completo en la base de datos
     *
     * @throws MasDeUnClienteEncontrado
     * @throws ClienteNoExisteException
     * @throws ArchivoNoCumpleParaSerClasificado
     * @throws MasDatosdeLosEsperadosException
     */
    private void procesar() throws MasDeUnClienteEncontrado, ClienteNoExisteException, ArchivoNoCumpleParaSerClasificado, MasDatosdeLosEsperadosException, TarifaNoExisteException {
        if (super.existeClientebyCups() && this.agregarClientePuntoSuministro() && this.agregarClienteContrato() && this.editarDatosCliente()) {
            super.clienteService.guardar(cliente);
        }
    }

    /**
     * Obtiene los datos buscando en el archivo xml los datos de
     * ClientePuntoSuministro
     *
     * @return
     * @throws ArchivoNoCumpleParaSerClasificado
     * @throws MasDatosdeLosEsperadosException
     */
    private boolean agregarClientePuntoSuministro() throws ArchivoNoCumpleParaSerClasificado, MasDatosdeLosEsperadosException {
        ClientePuntoSuministro cps = super.cliente.getClientePuntoSuministro();
        cps = cps != null ? cps : new ClientePuntoSuministro();
        try {
            cps.setDireccionSuministro("A1");
            cps.setCodigoPostal("B2");
            cps.setPoblacion("C3");
            cps.setProvincia("D4");
            cps.setDistribuidora(super.obtenerContenidoNodo(NombresNodos.COD_EMP_EMI));
            cps.setAtr(super.obtenerContenidoNodo(NombresNodos.COD_CONT));
            cps.setContador(super.obtenerContenidoNodo(NombresNodos.NUM_SER));
            cps.setTipoPM(super.obtenerContenidoNodo(NombresNodos.TIP_PM));
            cps.setModoLectura(super.obtenerContenidoNodo(NombresNodos.MOD_LEC));
            cps.setTarifa(super.obtenerContenidoNodo(NombresNodos.TAR_ATR));
            cps.setActivado(NombresNodos.ESTADOS.ACTIVADO.getEstado());
            this.agregarPotencias(cps);
            super.cliente.setClientePuntoSuministro(cps);
        } catch (NullPointerException e) {
            throw new ArchivoNoCumpleParaSerClasificado();
        }
        return true;
    }

    /**
     * Este metodo coloca los valores de potencia ordenandolos del 1 al 6
     * cliente punto suministro
     *
     * @param cps
     * @throws MasDatosdeLosEsperadosException
     */
    private void agregarPotencias(ClientePuntoSuministro cps) throws MasDatosdeLosEsperadosException {
        int elementos[] = new int[6];
        NodeList nodeList = super.doc.getElementsByTagName(NombresNodos.POT);
        try {
            for (int i = 0, size = nodeList.getLength(); i < size; i++) {
                elementos[StringHelper.toInteger(nodeList.item(i).getAttributes().getNamedItem(NombresNodos.PER).getNodeValue()) - 1] = super.obtenerContenidoNodoSobre1000(nodeList, i);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MasDatosdeLosEsperadosException(elementos.length, NombresNodos.POT);
        }
        cps.setPotencias(elementos);
    }

    /**
     * Obtiene los datos buscando en el archivo xml los datos de ClienteContrato
     *
     * @return
     * @throws ArchivoNoCumpleParaSerClasificado
     */
    private boolean agregarClienteContrato() throws ArchivoNoCumpleParaSerClasificado {
        ClienteContrato cc = super.cliente.getClienteContrato();
        cc = cc != null ? cc : new ClienteContrato();
        try {
            cc.setInicioContrato(super.obtenerContenidoNodo(NombresNodos.FEC));
            cc.setFinContrato("No proporcionado");
            cc.setActivado(NombresNodos.ESTADOS.ACTIVADO.getEstado());
            cc.setProducto(0);
            cc.setCosteGestion(0);
            cc.setAlquieres(0);
            super.cliente.setClienteContrato(cc);
        } catch (NullPointerException e) {
            throw new ArchivoNoCumpleParaSerClasificado();
        }
        return true;
    }

    /**
     * Edita los datos del cliente desde el archivo xml
     *
     * @return
     * @throws ArchivoNoCumpleParaSerClasificado
     */
    private boolean editarDatosCliente() throws ArchivoNoCumpleParaSerClasificado, TarifaNoExisteException {
        try {
            String tarifaATR = StringHelper.toInteger(super.obtenerContenidoNodo(NombresNodos.TAR_ATR)).toString();
            super.cliente.setTarifa(tarifaATR);
        } catch (NullPointerException e) {
            throw new ArchivoNoCumpleParaSerClasificado();
        }
        return validarCliente(super.cliente);
    }

    private boolean validarCliente(Cliente cliente) throws TarifaNoExisteException {
        boolean valido = false;
        List<Tarifa> tarifas = this.tarifasService.listar();
        for (Tarifa tarifa : tarifas) {
            if (tarifa.getTarifaCodigo().equals(cliente.getTarifa())) {
                cliente.setTarifa(tarifa.getNombreTarifa());
                valido = true;
                break;
            }
        }
        if (!valido) {
            throw new TarifaNoExisteException(cliente.getTarifa());
        }
        return valido;
    }

}

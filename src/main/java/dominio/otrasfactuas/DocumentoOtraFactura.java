package dominio.otrasfactuas;

import dominio.Cliente;

public class DocumentoOtraFactura {
    private Cliente cliente;
    private Cabecera cabecera;
    private DatosGeneralesFactura datosGeneralesFactura;
    private ConceptoRepercutible conceptoRepercutible;
    private RegistroFin registroFin;
    private String comentarios;
    private String errores;

    public DocumentoOtraFactura() {
    }

    public DocumentoOtraFactura(Cabecera cabecera, DatosGeneralesFactura datosGeneralesFactura, ConceptoRepercutible conceptoRepercutible, RegistroFin registroFin, String comentarios, String errores) {
        this.cabecera = cabecera;
        this.datosGeneralesFactura = datosGeneralesFactura;
        this.conceptoRepercutible = conceptoRepercutible;
        this.registroFin = registroFin;
        this.comentarios = comentarios;
        this.errores = errores;
    }

    public DocumentoOtraFactura(Cliente cliente, Cabecera cabecera, DatosGeneralesFactura datosGeneralesFactura, ConceptoRepercutible conceptoRepercutible, RegistroFin registroFin, String comentarios, String errores) {
        this.cliente = cliente;
        this.cabecera = cabecera;
        this.datosGeneralesFactura = datosGeneralesFactura;
        this.conceptoRepercutible = conceptoRepercutible;
        this.registroFin = registroFin;
        this.comentarios = comentarios;
        this.errores = errores;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    

    public Cabecera getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecera cabecera) {
        this.cabecera = cabecera;
    }

    public ConceptoRepercutible getConceptoRepercutible() {
        return conceptoRepercutible;
    }

    public void setConceptoRepercutible(ConceptoRepercutible conceptoRepercutible) {
        this.conceptoRepercutible = conceptoRepercutible;
    }

    public DatosGeneralesFactura getDatosGeneralesFactura() {
        return datosGeneralesFactura;
    }

    public void setDatosGeneralesFactura(DatosGeneralesFactura datosGeneralesFactura) {
        this.datosGeneralesFactura = datosGeneralesFactura;
    }

    public RegistroFin getRegistroFin() {
        return registroFin;
    }

    public void setRegistroFin(RegistroFin registroFin) {
        this.registroFin = registroFin;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getErrores() {
        return errores;
    }

    public void setErrores(String errores) {
        this.errores = errores;
    }

    @Override
    public String toString() {
        return "DocumentoOtrasFacturas{" + "cabecera=" + cabecera + ", conceptoRepercutible=" + conceptoRepercutible + ", datosGeneralesFactura=" + datosGeneralesFactura + ", registroFin=" + registroFin + '}';
    }
    
}

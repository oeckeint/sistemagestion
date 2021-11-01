package datos.entity;

import dominio.componentesxml.ConceptoRepercutible;
import dominio.componentesxml.DatosCabecera;
import dominio.componentesxml.DatosFinDeRegistro;
import dominio.componentesxml.DatosGeneralesFactura;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
@Entity
@Table(name = "contenido_xml_otras_facturas")
public class OtraFactura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cont")
    private long idOtraFactura;
    
    @Column(name = "is_deleted")
    private int isDeleted;
    
    @Column(name = "cod_emp_emi")
    private String codEmpEmi;
    
    @Column(name = "cod_emp_des")
    private String codEmpDes;
    
    @Column(name = "cod_pro")
    private String codPro;
    
    @Column(name = "cod_pas")
    private String codPas;
    
    @Column(name = "cod_sol")
    private String codSol;
    
    @Column(name = "id_cliente")
    private long idCliente;
    
    @Column(name = "cups")
    private String cups;
    
    @Column(name = "cod_fis_fac")
    private String codFisFac;
    
    @Column(name = "imp_tot_fac")
    private double impTotFac;
    
    @Column(name = "tip_fac")
    private String tipFac;
    
    @Column(name = "mot_fac")
    private String motFac;
    
    @Column(name = "fec_fac")
    private String fecFac;
    
    @Column(name = "com")
    private String com;
    
    @Column(name = "rf_imp_tot")
    private double rfImpTot;
    
    @Column(name = "con_rep")
    private String conRep;
    
    @Column(name = "imp_tot_con_rep")
    private double impTotConRep;
    
    @Column(name = "id_rem")
    private String idRem;
    
    @Column(name = "com_dev")
    private String comDev;
    
    @Column(name = "id_err")
    private String idErr;
    
    @Column(name = "remesa_pago")
    private String remesaPago;
    
    @Column(name = "estado_pago")
    private int estadoPago;

    public OtraFactura() {
    }

    public OtraFactura(
            Cliente cliente,DatosCabecera datosCabecera, DatosGeneralesFactura datosGeneralesFactura, 
            ConceptoRepercutible conceptoRepercutible, DatosFinDeRegistro datosFinDeRegistro, 
            String comentarios, String errores) {
        this.idCliente = cliente.getIdCliente();
        this.codEmpEmi = datosCabecera.getCodigoREEEmpresaEmisora();
        this.codEmpDes = datosCabecera.getCodigoREEEmpresaDestino();
        this.codPro = datosCabecera.getCodigoDelProceso();
        this.codPas = datosCabecera.getCodigoDePaso();
        this.codSol = datosCabecera.getCodigoDeSolicitud();
        this.cups = datosCabecera.getCups();
        this.codFisFac = datosGeneralesFactura.getCodigoFiscalFactura();
        this.impTotFac = datosGeneralesFactura.getImpTotFac();
        this.tipFac = datosGeneralesFactura.getTipoFactura();
        this.motFac = datosGeneralesFactura.getMotivoFacturacion();
        this.fecFac = datosGeneralesFactura.getFechaFactura();
        this.com = comentarios;
        this.rfImpTot = datosFinDeRegistro.getImporteTotal();
        this.conRep = conceptoRepercutible.getConRep();
        this.impTotConRep = conceptoRepercutible.getImpTotConRep();
        this.idRem = datosFinDeRegistro.getIdRemesa();
        this.comDev = comentarios;
        this.idErr = errores;
    }

    public long getIdOtraFactura() {
        return idOtraFactura;
    }

    public void setIdOtraFactura(long idOtraFactura) {
        this.idOtraFactura = idOtraFactura;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCodEmpEmi() {
        return codEmpEmi;
    }

    public void setCodEmpEmi(String codEmpEmi) {
        this.codEmpEmi = codEmpEmi;
    }

    public String getCodEmpDes() {
        return codEmpDes;
    }

    public void setCodEmpDes(String codEmpDes) {
        this.codEmpDes = codEmpDes;
    }

    public String getCodPro() {
        return codPro;
    }

    public void setCodPro(String codPro) {
        this.codPro = codPro;
    }

    public String getCodPas() {
        return codPas;
    }

    public void setCodPas(String codPas) {
        this.codPas = codPas;
    }

    public String getCodSol() {
        return codSol;
    }

    public void setCodSol(String codSol) {
        this.codSol = codSol;
    }

    public long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public String getCodFisFac() {
        return codFisFac;
    }

    public double getImpTotFac() {
        return impTotFac;
    }

    public void setImpTotFac(double impTotFac) {
        this.impTotFac = impTotFac;
    }

    public void setCodFisFac(String codFisFac) {
        this.codFisFac = codFisFac;
    }

    public String getTipFac() {
        return tipFac;
    }

    public void setTipFac(String tipFac) {
        this.tipFac = tipFac;
    }

    public String getMotFac() {
        return motFac;
    }

    public void setMotFac(String motFac) {
        this.motFac = motFac;
    }

    public String getFecFac() {
        return fecFac;
    }

    public void setFecFac(String fecFac) {
        this.fecFac = fecFac;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public double getRfImpTot() {
        return rfImpTot;
    }

    public void setRfImpTot(double rfImpTot) {
        this.rfImpTot = rfImpTot;
    }

    public String getConRep() {
        return conRep;
    }

    public void setConRep(String conRep) {
        this.conRep = conRep;
    }

    public double getImpTotConRep() {
        return impTotConRep;
    }

    public void setImpTotConRep(double impTotConRep) {
        this.impTotConRep = impTotConRep;
    }

    public String getIdRem() {
        return idRem;
    }

    public void setIdRem(String idRem) {
        this.idRem = idRem;
    }

    public String getComDev() {
        return comDev;
    }

    public void setComDev(String comDev) {
        this.comDev = comDev;
    }

    public String getIdErr() {
        return idErr;
    }

    public void setIdErr(String idErr) {
        this.idErr = idErr;
    }

    public String getRemesaPago() {
        return remesaPago;
    }

    public void setRemesaPago(String remesaPago) {
        this.remesaPago = remesaPago;
    }

    public int getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(int estadoPago) {
        this.estadoPago = estadoPago;
    }

    @Override
    public String toString() {
        return "OtraFactura{" + "idOtraFactura=" + idOtraFactura + ", isDeleted=" + isDeleted + ", codEmpEmi=" + codEmpEmi + ", codEmpDes=" + codEmpDes + ", codPro=" + codPro + ", codPas=" + codPas + ", codSol=" + codSol + ", idCliente=" + idCliente + ", cups=" + cups + ", codFisFac=" + codFisFac + ", tipFac=" + tipFac + ", motFac=" + motFac + ", fecFac=" + fecFac + ", com=" + com + ", rfImpTot=" + rfImpTot + ", conRep=" + conRep + ", impTotConRep=" + impTotConRep + ", idRem=" + idRem + ", comDev=" + comDev + ", idErr=" + idErr + ", remesaPago=" + remesaPago + ", estadoPago=" + estadoPago + '}';
    }
    
}

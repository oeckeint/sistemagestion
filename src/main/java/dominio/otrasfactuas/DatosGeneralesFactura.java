
package dominio.otrasfactuas;

import java.util.ArrayList;

public class DatosGeneralesFactura {
    private String codFisFac;
    private String tipFac;
    private String motFac;
    private String fecFac;
    private String com;
    private Double impTotFac;

    public DatosGeneralesFactura() {
    }

    public DatosGeneralesFactura(ArrayList<String> dat) {
        this.codFisFac = dat.get(0);
        this.tipFac = dat.get(1);
        this.motFac = dat.get(2);
        this.fecFac = dat.get(3);
        this.com = dat.get(4);
        this.impTotFac = Double.parseDouble(dat.get(5));
    }

    public String getCodFisFac() {
        return codFisFac;
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

    public Double getImpTotFac() {
        return impTotFac;
    }

    public void setImpTotFac(Double impTotFac) {
        this.impTotFac = impTotFac;
    }

    @Override
    public String toString() {
        return "DatosGeneralesFactura{" + "codFisFac=" + codFisFac + ", tipFac=" + tipFac + ", motFac=" + motFac + ", fecFac=" + fecFac + ", com=" + com + ", impTotFac=" + impTotFac + '}';
    }
    
}
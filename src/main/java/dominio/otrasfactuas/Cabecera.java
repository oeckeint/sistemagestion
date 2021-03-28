
package dominio.otrasfactuas;

import java.util.ArrayList;

public class Cabecera {
    
    private String codEmpEmi;
    private String codEmpDes;
    private String codPro;
    private String codPas;
    private String codSol;
    private String cups;

    public Cabecera() {
    }

    public Cabecera(ArrayList<String> datos) {
        this.codEmpEmi = datos.get(0);
        this.codEmpDes = datos.get(1);
        this.codPro = datos.get(2);
        this.codPas = datos.get(3);
        this.codSol = datos.get(4);
        this.cups = datos.get(5);
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

    public String getCups() {
        return cups;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    @Override
    public String toString() {
        return "Cabecera{" + "codEmpEmi=" + codEmpEmi + ", codEmpDes=" + codEmpDes + ", codPro=" + codPro + ", codPas=" + codPas + ", codSol=" + codSol + ", cups=" + cups + '}';
    }
    
}

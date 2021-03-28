
package dominio.otrasfactuas;

import java.util.ArrayList;

public class ConceptoRepercutible {
    
    private String conRep;
    private double impTot;

    public ConceptoRepercutible() {
    }

    public ConceptoRepercutible(ArrayList data) {
        this.conRep = (String) data.get(0);
        this.impTot = Double.parseDouble(String.valueOf(data.get(1)));
    }

    public String getConRep() {
        return conRep;
    }

    public void setConRep(String conRep) {
        this.conRep = conRep;
    }

    public double getImpTot() {
        return impTot;
    }

    public void setImpTot(double impTot) {
        this.impTot = impTot;
    }

    @Override
    public String toString() {
        return "ConceptoRepercutible{" + "conRep=" + conRep + ", impTot=" + impTot + '}';
    }
    
}

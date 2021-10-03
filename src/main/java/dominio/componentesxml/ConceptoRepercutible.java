package dominio.componentesxml;

import java.util.List;

/**
 *
 * @author Jesus Sanchez <j.sanchez at dataWorkshop>
 */
public class ConceptoRepercutible {
    
    private String conRep;
    private double impTotConRep;

    public ConceptoRepercutible() {
    }

    public ConceptoRepercutible(List<String> datos) {
        this.conRep = datos.get(0);
        this.impTotConRep = Double.parseDouble(datos.get(1));
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

    @Override
    public String toString() {
        return "ConceptoRepercutible{" + "conRep=" + conRep + ", impTotConRep=" + impTotConRep + '}';
    }
    
}

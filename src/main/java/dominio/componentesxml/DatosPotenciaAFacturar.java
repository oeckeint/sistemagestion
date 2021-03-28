
package dominio.componentesxml;

import java.util.List;

public class DatosPotenciaAFacturar {
    private double P1, P2, P3;

    public DatosPotenciaAFacturar() {
    }

    public DatosPotenciaAFacturar(List<Double> datos) {
        this.P1 = datos.get(0);
        this.P2 = datos.get(1);
        this.P3 = datos.get(2);
    }

    public double getP1() {
        return P1;
    }

    public double getP2() {
        return P2;
    }

    public double getP3() {
        return P3;
    }

    @Override
    public String toString() {
        return "DatosPotenciaAFacturar{" + "PotenciaAFacturar1=" + P1 + ", PotenciaAFacturar2=" + P2 + ", PotenciaAFacturar3=" + P3 + '}';
    }
    
    
}

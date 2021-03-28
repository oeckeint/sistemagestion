
package dominio.componentesxml;

import java.util.List;

public class DatosAeConsumo {
    
    private double consumoCalculado1;
    private double consumoCalculado2;
    private double consumoCalculado3;
    private double consumoCalculado4;
    private double consumoCalculado5;
    private double consumoCalculado6;
    private double suma;

    public DatosAeConsumo() {
    }

    public DatosAeConsumo(List <Double> datos) {
        this.consumoCalculado1 = datos.get(0);
        this.consumoCalculado2 = datos.get(1);
        this.consumoCalculado3 = datos.get(2);
        this.consumoCalculado4 = datos.get(3);
        this.consumoCalculado5 = datos.get(4);
        this.consumoCalculado6 = datos.get(5);
        this.suma = datos.get(6);
    }

    public double getConsumoCalculado1() {
        return consumoCalculado1;
    }

    public double getConsumoCalculado2() {
        return consumoCalculado2;
    }

    public double getConsumoCalculado3() {
        return consumoCalculado3;
    }

    public double getConsumoCalculado4() {
        return consumoCalculado4;
    }

    public double getConsumoCalculado5() {
        return consumoCalculado5;
    }

    public double getConsumoCalculado6() {
        return consumoCalculado6;
    }

    public double getSuma() {
        return suma;
    }

    @Override
    public String toString() {
        return "DatosAeConsumo{" + "consumoCalculado1=" + consumoCalculado1 + ", consumoCalculado2=" + consumoCalculado2 + ", consumoCalculado3=" + consumoCalculado3 + ", consumoCalculado4=" + consumoCalculado4 + ", consumoCalculado5=" + consumoCalculado5 + ", consumoCalculado6=" + consumoCalculado6 + ", suma=" + suma + '}';
    }
    
}

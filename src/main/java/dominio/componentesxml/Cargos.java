package dominio.componentesxml;

import java.util.List;

public class Cargos {

    private double c1;
    private double c2;
    private double c3;
    private double c4;
    private double c5;
    private double c6;
    private double total;

    public Cargos() {
    }

    public Cargos(List<Double> datos) {
		this.c1 = datos.get(0) + datos.get(6);
        this.c2 = datos.get(1) + datos.get(7);
        this.c3 = datos.get(2) + datos.get(8);
        this.c4 = datos.get(3) + datos.get(9);
        this.c5 = datos.get(4) + datos.get(10);
        this.c6 = datos.get(5) + datos.get(11);
        this.total = datos.stream().reduce(0.0, Double::sum);
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC2() {
        return c2;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }

    public double getC3() {
        return c3;
    }

    public void setC3(double c3) {
        this.c3 = c3;
    }

    public double getC4() {
        return c4;
    }

    public void setC4(double c4) {
        this.c4 = c4;
    }

    public double getC5() {
        return c5;
    }

    public void setC5(double c5) {
        this.c5 = c5;
    }

    public double getC6() {
        return c6;
    }

    public void setC6(double c6) {
        this.c6 = c6;
    }

    public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "Cargos [c1=" + c1 + ", c2=" + c2 + ", c3=" + c3 + ", c4=" + c4 + ", c5=" + c5 + ", c6=" + c6
				+ ", total=" + total + "]";
	}

}

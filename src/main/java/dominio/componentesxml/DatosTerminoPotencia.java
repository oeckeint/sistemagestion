package dominio.componentesxml;

import java.util.List;

public class DatosTerminoPotencia {
	private String FechaHasta, FechaDesde;
	
	public DatosTerminoPotencia() {}
	
	public DatosTerminoPotencia(List<String> datos) {
		this.FechaDesde = datos.get(0);
		this.FechaHasta = datos.get(1);
	}
	
	public String getFechaDesde() {
		return this.FechaDesde;
	}
	public void setFechaDesde (String FechaDesde) {
		this.FechaDesde = FechaDesde;
	}
	
	public String getFechaHasta () {
		return this.FechaHasta;
	}
	public void setFechaHasta (String fechaHasta){
		this.FechaHasta = fechaHasta;
	}

	@Override
	public String toString() {
		return "DatosTerminoPotencia [FechaHasta=" + FechaHasta + ", FechaDesde=" + FechaDesde + "]";
	}

}

package controladores.helper;

public class NombresNodos {

    public static String COD_CONT = "CodContrato";
    public static String COD_EMP_EMI = "CodigoREEEmpresaEmisora";
    public static String COD_POS = " ";
    public static String DIR_SUM = "";
    public static String FEC = "Fecha";
    public static String MOD_LEC = "ModoLectura";   
    public static String NUM_SER = "NumeroSerie";
    public static String PER = "Periodo";
    public static String POB = "";
    public static String POT = "Potencia";
    public static String TAR_ATR = "TarifaATR";
    public static String TIP_PM = "TipoPM";
    
    /**
     * Valores disponibles para los diferentes status
     */
    public static enum ESTADOS {
        //0 - Baja\n1 - Activado\n2 - AvisoUltimoMes\n3 - UltimoMes\n4 - Renovado\n5 - Aceptado
        BAJA(0), ACTIVADO(1), AVISO_ULTIMO_MES(2), ULTIMO_MES(3), RENOVADO(4), ACEPTADO(5);
        
        private ESTADOS(int estado) {
            this.estado = estado;
        }
        
        public int getEstado(){
            return this.estado;
        }
        
        private final int estado;
        
    }

}

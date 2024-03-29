package controladores.helper;

public class NombresNodos {

    public static String COD_CONT = "CodContrato";
    public static final String COD_FIS_FAC = "CodigoFiscalFactura";
    public static final String COD_FAC_REC_ANU = "CodigoFacturaRectificadaAnulada";
    public static final String ID_REM = "IdRemesa";
    public static String COD_PAS = "CodigoDePaso";
    public static String COD_PRO = "CodigoDelProceso";
    public static String COD_SOL = "CodigoDeSolicitud";
    public static String COD_EMP_EMI = "CodigoREEEmpresaEmisora";
    public static String COD_POS = "";//Pendiente por averiguarse
    public static String CUPS = "CUPS";
    public static String DIR_SUM = "";
    public static String EMP_EMI = "CodigoREEEmpresaEmisora";
    public static String EMP_DES = "CodigoREEEmpresaDestino";
    public static String FEC = "Fecha";
    public static final String FECHA_LIMITE_PAGO = "FechaLimitePago";
    public static final String IMPORTE_TOTAL_TERMINO_POTENCIA = "ImporteTotalTerminoPotencia";
    public static String MOD_LEC = "ModoLectura";   
    public static String MOT_FAC = "MotivoFacturacion";
    public static final String NUMERO_DIAS = "NumeroDias";
    public static String NUM_SER = "NumeroSerie";
    public static String PER = "Periodo";
    public static String POB = "";//Pendiente por averiguarse
    public static String POT = "Potencia";
    public static String TAR_ATR = "TarifaATR";
    public static String TIP_FAC = "TipoFactura";
    public static String TIP_PM = "TipoPM";
    public static String CON_FAC = "ControlFacturacion";
    public static String REM_PAG = "RemesaPago";
    public static String IS_DEL = "isDeleted";
    
    /** My declarations **/
    public static final String FIL = "Filtro";
    public static String FAC = "Factura";
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

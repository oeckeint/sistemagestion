package test;

public class ValidaCUPS {

    String CUPS = "a";
    boolean VerifyCups;
    int longitud = CUPS.length();

    public void validar() {
        if (this.longitud < 23 && this.longitud > 19) {

            String valor = CUPS.substring(3, 16);
            String Control = CUPS.substring(19, 2); //EE
            long num = Long.parseLong(Control);
            float aux = (num % 529) / 23;
            int parteDecimal = (int) aux % 1;
            int parteEntera = (int) (aux - parteDecimal);

            String Letra1 = this.LetraCups(parteEntera);
            String Letra2 = this.LetraCups(parteDecimal);

            if (Control.equals(Letra1 + Letra2)) {
                VerifyCups = true;
            } else {
                VerifyCups = false;
            }
        } else {
            VerifyCups = false;
            // "Debe estar entre 20 y 22 caracteres"
        }

        /*   P para identificar punto de medida principal,
		                                                        '   R para identificar punto de medida redundante,
		                                                        '   C para identificar punto de medida comprobante y
		                                                        '   X, Y o Z para identificar registradores.
         */
 /* EE- 2 caracteres alfabéticos en mayúsculas (para control y detección de errores),calculados: '
		        '   Se divide el número natural formado por los 16 dígitos del código (DDDD CCCC CCCC CCCC), entre 529
		        '   obteniéndose un cociente (que no se utiliza) y un resto R0 (equivalente a la operación DDDDCCCCCCCCCCCC Módulo 529).
		        '   A continuación se divide el resto R0 anterior entre 23 obteniéndose un cociente C y un resto R (equivalentes, respectivamente,
		        '   a parte entera de R0/23 y R0 Módulo 23). Las letras de control serán las resultantes de transformar C y R (en este orden)
         */
    }

    private String LetraCups(int num) {

        String LetraControl = "";
        switch (num) {
            case 0:
                LetraControl = "T";
                break;
            case 1:
                LetraControl = "R";
                break;
            case 2:
                LetraControl = "W";
                break;
            case 3:
                LetraControl = "A";
                break;
            case 4:
                LetraControl = "G";
                break;
            case 5:
                LetraControl = "M";
                break;
            case 6:
                LetraControl = "Y";
                break;
            case 7:
                LetraControl = "F";
                break;
            case 8:
                LetraControl = "P";
                break;
            case 9:
                LetraControl = "D";
                break;
            case 10:
                LetraControl = "X";
                break;
            case 11:
                LetraControl = "B";
                break;
            case 12:
                LetraControl = "N";
                break;
            case 13:
                LetraControl = "J";
                break;
            case 14:
                LetraControl = "Z";
                break;
            case 15:
                LetraControl = "S";
                break;
            case 16:
                LetraControl = "Q";
                break;
            case 17:
                LetraControl = "V";
                break;
            case 18:
                LetraControl = "H";
                break;
            case 19:
                LetraControl = "L";
                break;
            case 20:
                LetraControl = "C";
                break;
            case 21:
                LetraControl = "K";
                break;
            case 22:
                LetraControl = "E";
                break;
            default:
                LetraControl = "0";
                break;

            /*  Valor de C ó R: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22
            Letra  a  usar: T R W A G M Y F P D  X  B  N  J  Z  S  Q  V  H  L  C  K  E */
        }
        return LetraControl;
    }

}

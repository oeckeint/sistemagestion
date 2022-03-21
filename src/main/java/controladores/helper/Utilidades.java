package controladores.helper;

public class Utilidades {

    public static int revisarPaginaActual(int paginaActual){
        if (paginaActual < 1) {
            paginaActual = 1;
        }
        return paginaActual;
    }

    public static int revisarRangoRows(int rows, int rango) {
        if (rows % rango != 0) {
            if (rows <= rango) {
                rows = rango;
            } else if (rows >= (rango * 6)) {
                rows = rango * 6;
            } else {
                rows = rango * 2;
            }
        } else {
            if (rows <= rango) {
                rows = rango;
            } else if (rows >= (rango * 6)) {
                rows = rango * 6;
            }
        }
        return rows;
    }

}

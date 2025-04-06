package utileria;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PaginacionUtil {
    public static <T> List<T> validarPaginacion(int rows, int page, Supplier<List<T>> listaSupplier) {
        if (rows < 1 || page < 0) {
            return Collections.emptyList();
        }
        return listaSupplier.get();
    }
}

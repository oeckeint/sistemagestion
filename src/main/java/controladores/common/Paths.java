package controladores.common;

import common.i18n.Messages;
import common.i18n.SGECommonMessageKey;

public class Paths {

    public static final String BASE = "/sistemagestion/";

    private Paths() {
        throw new IllegalStateException(Messages.get(SGECommonMessageKey.UTILITY_CLASS));
    }

    public static final class Procesamiento {
        public static final String PROCESAR = BASE + "procesar";

        private Procesamiento() {
            throw new IllegalStateException(Messages.get(SGECommonMessageKey.UTILITY_CLASS));
        }
    }

}

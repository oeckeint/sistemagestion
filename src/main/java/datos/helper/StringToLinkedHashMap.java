package datos.helper;

import org.springframework.core.convert.converter.Converter;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StringToLinkedHashMap implements Converter<String, LinkedHashMap<String, String>> {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public LinkedHashMap<String, String> convert(String data) {
        logger.log(Level.INFO, ">>> convirtiendo String a LinkedHashMap con {0}", data);
        LinkedHashMap map = new LinkedHashMap<String, String>();
        StringBuilder sb = new StringBuilder(data);

        if (data != null && data.length() > 2){
            data = data.substring(1, data.length() - 1);
            String[] elementos = data.split(",");
            for (int i = 0; i < elementos.length; i++){
                String[] subElemento = elementos[i].split("=");
                map.put(subElemento[0].trim(), subElemento[1].trim());
            }
        }

        return map;
    }
}

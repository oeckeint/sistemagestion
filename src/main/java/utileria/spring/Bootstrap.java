package utileria.spring;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Bootstrap {

    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;

        try {
            ClassPathResource resource = new ClassPathResource("banner.txt");
            if (resource.exists()) {
                String banner = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                System.out.println(banner);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el banner...");
        }

        // Configuración de puentes de Logback
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        initialized = true;
    }


}

package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class StoragePathsInitializer {

    private static final Logger logger = Logger.getLogger(StoragePathsInitializer.class.getName());

    private static final List<String> REQUIRED_PATH_KEYS = Arrays.asList(
            "peajes",
            "peajes.procesados",
            "peajes.remesa",
            "peajes.archivarfactura",
            "peajes.ftp",
            "sql.backup",
            "sql.restauraciones",
            "scripts.ruta"
    );

    @Autowired
    private Environment env;

    @PostConstruct
    public void initializeStoragePaths() {
        for (String key : REQUIRED_PATH_KEYS) {
            String configuredPath = env.getProperty(key);
            if (configuredPath == null || configuredPath.trim().isEmpty()) {
                throw new IllegalStateException("No se encontro la propiedad requerida: " + key);
            }
            ensureDirectory(key, configuredPath.trim());
        }
        logger.info("Inicializacion de rutas completada correctamente.");
    }

    public Map<String, PathStatus> inspectConfiguredPaths() {
        Map<String, PathStatus> statuses = new LinkedHashMap<>();
        for (String key : REQUIRED_PATH_KEYS) {
            statuses.put(key, inspectPath(key));
        }
        return statuses;
    }

    public boolean allPathsHealthy(Map<String, PathStatus> statuses) {
        for (PathStatus status : statuses.values()) {
            if (!status.isHealthy()) {
                return false;
            }
        }
        return true;
    }

    private PathStatus inspectPath(String key) {
        String configuredPath = env.getProperty(key);
        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            return PathStatus.missingProperty("No se encontro la propiedad requerida: " + key);
        }

        Path path = Paths.get(configuredPath.trim()).normalize().toAbsolutePath();
        boolean exists = Files.exists(path);
        boolean directory = exists && Files.isDirectory(path);
        boolean writable = exists && Files.isWritable(path);
        boolean healthy = exists && directory && writable;
        String detail = healthy
                ? "OK"
                : "No cumple validacion (exists=" + exists + ", directory=" + directory + ", writable=" + writable + ")";

        return new PathStatus(path.toString(), exists, directory, writable, healthy, detail);
    }

    private void ensureDirectory(String key, String configuredPath) {
        Path path = Paths.get(configuredPath).normalize();

        try {
            Files.createDirectories(path);

            if (!Files.exists(path) || !Files.isDirectory(path)) {
                throw new IllegalStateException("La ruta no es un directorio valido: " + path.toAbsolutePath());
            }

            if (!Files.isWritable(path)) {
                throw new IllegalStateException("La ruta no tiene permisos de escritura: " + path.toAbsolutePath());
            }

            logger.info("Ruta preparada [" + key + "]: " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible crear/verificar la ruta [" + key + "]: " + path.toAbsolutePath(),
                    e
            );
        }
    }

    public static class PathStatus {
        private final String path;
        private final boolean exists;
        private final boolean directory;
        private final boolean writable;
        private final boolean healthy;
        private final String detail;

        public PathStatus(String path, boolean exists, boolean directory, boolean writable, boolean healthy, String detail) {
            this.path = path;
            this.exists = exists;
            this.directory = directory;
            this.writable = writable;
            this.healthy = healthy;
            this.detail = detail;
        }

        public static PathStatus missingProperty(String detail) {
            return new PathStatus("", false, false, false, false, detail);
        }

        public String getPath() {
            return path;
        }

        public boolean isExists() {
            return exists;
        }

        public boolean isDirectory() {
            return directory;
        }

        public boolean isWritable() {
            return writable;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public String getDetail() {
            return detail;
        }
    }
}



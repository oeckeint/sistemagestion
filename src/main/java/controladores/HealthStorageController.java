package controladores;

import app.config.StoragePathsInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthStorageController {

    @Autowired
    private StoragePathsInitializer storagePathsInitializer;

    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> storageHealth() {
        Map<String, StoragePathsInitializer.PathStatus> pathStatuses = storagePathsInitializer.inspectConfiguredPaths();
        boolean healthy = storagePathsInitializer.allPathsHealthy(pathStatuses);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", healthy ? "UP" : "DOWN");
        response.put("component", "storage");
        response.put("checkedAt", Instant.now().toString());
        response.put("paths", pathStatuses);

        return new ResponseEntity<>(response, healthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE);
    }
}


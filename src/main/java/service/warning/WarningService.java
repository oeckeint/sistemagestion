package service.warning;

import common.i18n.Messages;
import common.publisher.incident.publisher.model.DataKeys;
import common.publisher.incident.publisher.model.DocumentWarning;
import controladores.common.ControladoresMessagesLogger;
import controladores.common.WarningType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class WarningService {

    private static final ControladoresMessagesLogger CONTROLADORES_MESSAGES_LOGGER = new ControladoresMessagesLogger();

    public void emitAndRegister(WarningType type, Consumer<WarningContext.Builder> builderConsumer) {

        WarningContext.Builder builder = WarningContext.builder();
        builderConsumer.accept(builder);

        WarningContext warningContext = builder.build();

        // 1. Formateo de mensaje
        String message = Messages.format(
                type.getMessageKey(),
                warningContext.getFileName(),
                warningContext.getData().get(DataKeys.TIPO_AUTOCONSUMO) // puedes mejorar esto luego
        );

        // 3. Log
        CONTROLADORES_MESSAGES_LOGGER.warn(log, message);

        // 4. Construcción del warning
        DocumentWarning warning = DocumentWarning.builder()
                .warning(type)
                .messageOverride(message)
                .endpoint(warningContext.getEndpoint())
                .httpMethod(warningContext.getHttpMethod())
                .fileName(warningContext.getFileName())
                .fileType(warningContext.getFileType())
                .flow(warningContext.getFlow())
                .data(warningContext.getData())
                .technicalContext(warningContext.getTechnicalContext())
                .build();

        // 5. Publicación
        utileria.ArchivoTexto.publishWarning(warning);

        // 6. Registro de error
        warningContext.getErrorConsumer().accept(type.getCode());
    }
}

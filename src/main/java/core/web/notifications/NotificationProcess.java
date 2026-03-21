package core.web.notifications;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationProcess {

    private final NotificationType type;
    private final String message;

    public enum NotificationType {
        SUCCESS,
        ERROR,
        WARNING,
        INFO
    }

}


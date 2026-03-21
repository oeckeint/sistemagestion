package core.web.flash;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlashType {

    ERROR("flashError"),
    WARNING("flashWarning"),
    INFO("flashInfo");

    private final String attributeName;

}

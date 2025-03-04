package utileria.texto;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Double> parseStringListToDoubleList(@NonNull List<String> stringList) {
        return stringList.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    public static LocalDateTime parseStringToLocalDateTimeWithDefaultTime(@NonNull String stringDate) {
        return LocalDateTime.parse(stringDate + "T00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}

package utileria;

import java.math.BigDecimal;

public class StringHelper {

    /**
     * Checks to see if a given string is valid. This includes checking that the
     * string is not null or empty.
     *
     * @param value - the string that is being evaluated
     * @return boolean - whether the string is valid
     */
    public static boolean isValid(String value) {
        boolean validValue = false;
        if (value != null) {
            String trimmedString = value.trim();
            if ((trimmedString.length() > 0) && !trimmedString.equalsIgnoreCase("null")) {
                validValue = true;
            }
        }
        return validValue;
    }

    /**
     * Checks to see if a given string is valid. This includes checking that the
     * string is not null or empty. If the second param is true, then strings
     * that equal the string "null" will be considered valid.
     *
     * @param value - the string that is being evaluated
     * @param displayNull - true if "null" is allowed
     * @return boolean - whether the string is valid
     */
    public static boolean isValid(String value, boolean displayNull) {
        boolean validValue = false;

        // if displayNull is false, use the one arg version of isValid
        if (!displayNull) {
            return isValid(value);
        }

        if (value != null) {
            String trimmedString = value.trim();

            if ((trimmedString.length() > 0)) {
                validValue = true;
            }
        }
        return validValue;
    }

    /**
     * Checks to see if a given string is valid. This includes checking that the
     * string is not null or empty. !! USE THIS METHOD ONLY FOR INPUT FROM
     * VIEW!!
     *
     * @param value - the string that is being evaluated
     * @return boolean - whether the string is valid
     */
    public static boolean isValidString(String value) {
        boolean validValue = false;

        if (value != null) {
            String trimmedString = value.trim();

            if ((trimmedString.length() > 0)) {
                validValue = true;
            }
        }

        return validValue;
    }

    /**
     * Checks to see if a given string is valid. This includes checking that the
     * string is ten characters in length. The second parameter also checks to
     * see if the string is all zeros.
     *
     * @param value - the string that is being evaluated.
     * @return boolean - return true if string is ten digits in length and the
     * string is not zero, else return false
     */
    public static boolean isValidPhone(String value) {
        BigDecimal decimal = new BigDecimal(String.valueOf(Double.parseDouble(value)));
        String valid = decimal.toPlainString();
        return (valid.length() == 10 && !(Long.parseLong(value) == 0));
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param input String
     * @return String Converted String
     */
    public static String capitalizeFirstChar(String input) {
        /*
		 * set initial string to input so that if string is not changed, exact input will be
		 * returned
         */
        String capitalizedString = input;

        if (StringHelper.isValid(input, true)) {
            char[] chars = input.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            capitalizedString = new String(chars);
        }

        return capitalizedString;
    }

    /**
     * Creates a string from an object
     *
     * @param objectValue - object to be translated
     * @return String - string value equal to the passed in object
     */
    public static String castToString(Object objectValue) {
        String stringValue = null;

        if (objectValue != null) {
            stringValue = (String) objectValue;
        }

        return stringValue;
    }

    /**
     * Helper method that decides if two fields are the same - equal or both
     * null or empty.
     *
     * @param value1 the first String to be compared
     * @param value2 the second string to be compared
     * @return true if the two Strings are equal or both null or empty
     */
    public static boolean equalsTreatNullsAsEmpty(String value1, String value2) {
        boolean result = false;
        if (value1 == null || value1.equals("")) {
            // checks if value2 is equal to null or empty string
            // and assigns true to the result else assigns false.
            result = (value2 == null || value2.equals("")) ? true : false;
        } else {
            result = value1.equals(value2);
        }

        return result;
    }

    /**
     * Deletes all occurences of the specified character within a String
     *
     * @param value - the string to translate
     * @param character - the character to remove from the string
     * @return String - a string value without any spaces
     */
    public static String removeCharacter(String value, char character) {
        if (isValid(value)) {
            StringBuffer stringValue = new StringBuffer(value);
            int index = value.indexOf(character);

            while (index > -1) {
                stringValue.deleteCharAt(index);
                index = stringValue.toString().indexOf(character);
            }

            value = stringValue.toString();
        }

        return value;
    }

    /**
     * Creates a Double from a String value
     *
     * @param stringValue - the string to translate
     * @returns Double - a numerical value equal to the passed in string
     */
    public static Double toDouble(String stringValue) {
        Double doubleValue = null;

        if (stringValue != null) {
            String trimmedString = stringValue.trim();

            // checks that string is not empty
            if (trimmedString.length() > 0) {
                doubleValue = new Double(trimmedString);
            }
        }

        return doubleValue;
    }

    /**
     * Creates an Integer from a string value
     *
     * @param stringValue - the string to translate
     * @returns Integer - a numerical value equal to the passed in string
     */
    public static Integer toInteger(String stringValue) {
        Integer integerValue = null;

        if (stringValue != null) {
            String trimmedString = stringValue.trim();

            // checks that string is not empty
            if (trimmedString.length() > 0) {
                integerValue = Integer.valueOf(trimmedString);
            }
        }

        return integerValue;
    }

    /**
     * Truncates the given string.
     *
     * @param value - the string to translate
     * @param nChars - the number of characters to display
     * @returns String - a string value with the first nChars displayed.
     */
    public static String truncate(String value, int nChars) {
        String newValue = null;

        if ((value != null) && (value.length() > nChars)) {
            newValue = value.substring(0, nChars);
        }

        return newValue;
    }

    /**
     * Returns the empty string ("") if the given String is null.
     *
     * @param value - the string to check for null.
     * @returns String - a string value that is "" or the original value.
     */
    public static String valueOf(Object value) {
        return ((value == null) ? "" : value.toString());
    }
}

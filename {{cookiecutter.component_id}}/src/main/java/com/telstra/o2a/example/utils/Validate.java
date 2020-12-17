package {{cookiecutter.group_id}}.example.utils;

public class Validate {
    private Validate() { }

    /**
     * @param str
     * @return true if the string is null or empty.
     */
    public static boolean isEmpty(final String str) {
        return (str == null || str.trim().length() == 0);
    }
    /**
     * @param str
     * @return true if the String length greater than 1
     */
    public static boolean isValid(final String str) {
        return (str != null && str.trim().length() > 1);
    }

}

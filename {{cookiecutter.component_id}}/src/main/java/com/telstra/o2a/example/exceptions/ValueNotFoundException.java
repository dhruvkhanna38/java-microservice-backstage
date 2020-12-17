package {{cookiecutter.group_id}}.example.exceptions;

public class ValueNotFoundException extends Exception {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Parameterized constructor.
     *
     * @param msg
     *            Exception message
     **/
    public ValueNotFoundException(final String msg) {
        super(msg);
    }
}

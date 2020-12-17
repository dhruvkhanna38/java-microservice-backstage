package {{cookiecutter.group_id}}.example.exceptions;

public class UnexpectedValueException extends RuntimeException {
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
    public UnexpectedValueException(final String msg) {
        super(msg);
    }

}

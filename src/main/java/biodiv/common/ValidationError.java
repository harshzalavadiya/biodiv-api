package biodiv.common;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Default validation error entity to be included in {@code Response}.
 *
 */
@XmlRootElement
@SuppressWarnings("UnusedDeclaration")
public final class ValidationError {

    private String message;

    private String path;

    //private String invalidValue;

    /**
     * Create a {@code ValidationError} instance. Constructor for JAXB providers.
     */
    public ValidationError() {
    }

    /**
     * Create a {@code ValidationError} instance.
     *
     * @param message interpolated error message.
     * @param messageTemplate non-interpolated error message.
     * @param path property path.
     * @param invalidValue value that failed to pass constraints.
     */
    public ValidationError(final String message, final String path) {
        this.message = message;
        //this.messageTemplate = messageTemplate;
        this.path = path;
    }

    /**
     * Return the interpolated error message for this validation error.
     *
     * @return the interpolated error message for this validation error.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Return the interpolated error message for this validation error.
     *
     * @param message the interpolated error message for this validation error.
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Return the string representation of the property path to the value.
     *
     * @return the string representation of the property path to the value.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the string representation of the property path to the value.
     *
     * @param path the string representation of the property path to the value.
     */
    public void setPath(final String path) {
        this.path = path;
    }


}

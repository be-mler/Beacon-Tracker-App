package saarland.cispa.bletrackerlib.exceptions;

public class TrackerException extends Exception {

    public TrackerException() {
    }

    public TrackerException(String message) {
        super(message);
    }

    public TrackerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackerException(Throwable cause) {
        super(cause);
    }
}

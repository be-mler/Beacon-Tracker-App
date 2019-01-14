package saarland.cispa.bletrackerlib.exceptions;

public class ParseException extends TrackerException {

    public ParseException() {
        super("Error while parsing beacon");
    }

    public ParseException(Throwable cause) {
        super("Error while parsing beacon", cause);
    }
}

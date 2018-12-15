package saarland.cispa.bletrackerlib.exceptions;

public class SimpleBeaconParseException extends TrackerException {

    public SimpleBeaconParseException() {
        super("Error while parsing beacon");
    }

    public SimpleBeaconParseException(Throwable cause) {
        super("Error while parsing beacon", cause);
    }
}

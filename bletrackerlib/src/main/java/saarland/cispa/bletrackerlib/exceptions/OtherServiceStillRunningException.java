package saarland.cispa.bletrackerlib.exceptions;

public class OtherServiceStillRunningException extends TrackerException {

    public OtherServiceStillRunningException() {
        super("A service already is running! You can not start more than one service.");
    }

    public OtherServiceStillRunningException(Throwable cause) {
        super("A service already is running! You can not parse more than one service.", cause);
    }
}

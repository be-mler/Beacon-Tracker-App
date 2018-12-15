package saarland.cispa.bletrackerlib.exceptions;

public class ServiceAlreadyExistsException extends TrackerException {

    public ServiceAlreadyExistsException() {
        super("A service already is running! You can not start more than one service.");
    }

    public ServiceAlreadyExistsException(Throwable cause) {
        super("A service already is running! You can not create more than one service.", cause);
    }
}

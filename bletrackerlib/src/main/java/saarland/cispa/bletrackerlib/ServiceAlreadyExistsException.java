package saarland.cispa.bletrackerlib;

public class ServiceAlreadyExistsException extends Exception {

    ServiceAlreadyExistsException() {
        super("A service already is running! You can not start more than one service.");
    }

    ServiceAlreadyExistsException(Throwable cause) {
        super("A service already is running! You can not create more than one service.", cause);
    }


}

package app.cleancode.jrjson;

public class InvalidJsonException extends Exception {
    private static final long serialVersionUID = 8690996148013310052L;

    InvalidJsonException(String message) {
        super(message);
    }

}

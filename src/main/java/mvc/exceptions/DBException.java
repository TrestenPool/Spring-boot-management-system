package mvc.exceptions;

public class DBException extends RuntimeException {
    public DBException(String msg) {
        super(msg);
    }

    public DBException(Exception e) {
        super(e);
    }
}

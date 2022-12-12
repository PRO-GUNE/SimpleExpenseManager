package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception;

public class InvalidTransactionException extends Throwable {
    public InvalidTransactionException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidTransactionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}

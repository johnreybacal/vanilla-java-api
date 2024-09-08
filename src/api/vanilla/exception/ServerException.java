package api.vanilla.exception;

/**
 * Exception with status code
 */
public class ServerException extends Exception {

    final private int statusCode;

    public ServerException(Throwable e) {
        super(e);
        this.statusCode = 500;
    }

    public ServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static ServerException notFound() {
        return new ServerException(404, "Resource not found.");
    }

}

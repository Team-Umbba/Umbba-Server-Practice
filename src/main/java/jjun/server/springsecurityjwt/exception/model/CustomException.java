package jjun.server.springsecurityjwt.exception.model;

import jjun.server.springsecurityjwt.exception.Error;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final Error error;

    public CustomException(Error error, String message) {
        super(message);
        this.error = error;
    }

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public int getHttpStatus() {
        return error.getHttpStatusCode();
    }
}

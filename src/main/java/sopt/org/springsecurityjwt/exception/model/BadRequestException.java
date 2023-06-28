package sopt.org.springsecurityjwt.exception.model;

import sopt.org.springsecurityjwt.exception.Error;

public class BadRequestException extends SoptException {
    public BadRequestException(Error error, String message) {
        super(error, message);
    }
}

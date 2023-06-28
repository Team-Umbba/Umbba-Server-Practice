package sopt.org.springsecurityjwt.exception.model;

import sopt.org.springsecurityjwt.exception.Error;

public class ConflictException extends SoptException {
    public ConflictException(Error error, String message) {
        super(error, message);
    }
}

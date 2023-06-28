package sopt.org.springsecurityjwt.exception.model;

import sopt.org.springsecurityjwt.exception.Error;

public class NotFoundException extends SoptException {
    public NotFoundException(Error error, String message) {
        super(error, message);
    }
}

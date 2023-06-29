package sopt.org.springsecurityjwt.exception.model;

import sopt.org.springsecurityjwt.exception.Error;

public class RefreshExpirationException extends SoptException {
    public RefreshExpirationException(Error error, String message) {
        super(error, message);
    }
}
package sopt.org.springsecurityjwt.exception.model;

import lombok.Getter;
import sopt.org.springsecurityjwt.exception.Error;

@Getter
public class UnauthorizedException extends SoptException{
    public UnauthorizedException(Error error, String message) {
        super(error, message);
    }
}

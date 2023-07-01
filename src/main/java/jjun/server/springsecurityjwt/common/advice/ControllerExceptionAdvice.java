package jjun.server.springsecurityjwt.common.advice;

import jjun.server.springsecurityjwt.common.dto.ApiResponse;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.model.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 발생가능한 Error를 구체적으로 명시하여 예외처리를 하는 클래스
 * 커스텀 예외처리도 여기에서 처리할 수 있다.
 * try-catch 구문을 강제하지 않는 Unchecked Exception 방식
 */
@RestControllerAdvice
public class ControllerExceptionAdvice {

    /**
     * 400 Bad Request
     */
    // @Valid 어노테이션이 붙은 Controller 단의 예외를 잡아준다. (DTO에서 지정)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        // 각 필드에서 발생한 에러에 대해 상세 메시지를 처리해줄 수 있다. -> 필드 에러를 발생시킨 값 + 에러 메시지 반환
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        return ApiResponse.error(Error.REQUEST_VALIDATION_EXCEPTION, String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

    // Header에 원하는 Key가 없는 경우
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ApiResponse<Object> handlerMissingRequestHeaderException(final MissingRequestHeaderException e) {
        return ApiResponse.error(Error.HEADER_REQUEST_MISSING_EXCEPTION, Error.HEADER_REQUEST_MISSING_EXCEPTION.getMessage());
    }

    /*@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    protected ApiResponse<Object> handlerBadRequestException(final BadRequestException e) {
        return ApiResponse.error(e.getError(), e.getMessage());
    }*/



    /**
     * 500 Server Error
     * * 서비스 단에서 예외가 꼼꼼하게 처리된 상태에서 500에러를 던지는 게 좋다
     */
    /*@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Object> handleException(final Exception e) {
        System.out.println(e.getMessage());
        return ApiResponse.error(Error.INTERNAL_SERVER_ERROR);
    }*/


    /**
     * CustomException : 커스텀 예외처리 클래스에서 발생한 에러를 잡아준다.
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getError(), e.getMessage()));
    }
}

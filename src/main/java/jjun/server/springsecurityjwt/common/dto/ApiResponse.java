package jjun.server.springsecurityjwt.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jjun.server.springsecurityjwt.exception.Error;
import jjun.server.springsecurityjwt.exception.Success;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonPropertyOrder({"httpStatusCode", "httpReasonPhrase", "message", "result"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final int httpStatusCode;
    private final String httpReasonPhrase;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static ApiResponse success(Success success) {
        return new ApiResponse<>(success.getHttpStatusCode(), success.getHttpStatus().getReasonPhrase(), success.getMessage());
    }

    public static <T> ApiResponse<T> success(Success success, T data) {
        return new ApiResponse<T>(success.getHttpStatusCode(), success.getHttpStatus().getReasonPhrase(), success.getMessage(), data);
    }

    public static ApiResponse error(Error error) {
        return new ApiResponse<>(error.getHttpStatusCode(), error.getHttpStatus().getReasonPhrase(), error.getMessage());
    }

    public static ApiResponse error(Error error, String message) {
        return new ApiResponse<>(error.getHttpStatusCode(), error.getHttpStatus().getReasonPhrase(), message);
    }
}

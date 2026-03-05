package com.nextdaydelivery.global.exception;

import com.nextdaydelivery.global.domain.error.AuthErrorCode;
import com.nextdaydelivery.global.domain.error.ErrorCode;
import com.nextdaydelivery.global.domain.error.GlobalErrorCode;
import com.nextdaydelivery.global.dto.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ex.getErrorCode();

        log.warn("[BUS] {} {} | {}: {}",
                request.getMethod(),
                request.getRequestURI(),
                errorCode.getCode(),
                ex.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode));
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<CommonResponse<List<CommonResponse.ValidationErrorDetail>>> handleValidationException(
            BindException ex,
            HttpServletRequest request
    ) {

        log.warn("[VAL] {} {} | Validation Failed",
                request.getMethod(),
                request.getRequestURI());

        List<CommonResponse.ValidationErrorDetail> details =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> new CommonResponse.ValidationErrorDetail(
                                error.getField(),
                                error.getDefaultMessage()
                        ))
                        .collect(Collectors.toList());

        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode, details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<List<CommonResponse.ValidationErrorDetail>>> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        log.warn("[VAL] {} {} | Constraint Violated",
                request.getMethod(),
                request.getRequestURI());

        List<CommonResponse.ValidationErrorDetail> details =
                ex.getConstraintViolations()
                        .stream()
                        .map(violation -> new CommonResponse.ValidationErrorDetail(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        ))
                        .collect(Collectors.toList());

        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode, details));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<CommonResponse<Void>> handleBadRequest(
            Exception ex,
            HttpServletRequest request
    ) {

        log.warn("[BAD] {} {} | {}: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage());

        ErrorCode errorCode = switch (ex) {
            case NoHandlerFoundException ignored -> GlobalErrorCode.NOT_FOUND;
            case HttpRequestMethodNotSupportedException ignored -> GlobalErrorCode.METHOD_NOT_ALLOWED;
            case IllegalArgumentException ignored -> GlobalErrorCode.INVALID_INPUT_VALUE;
            case MethodArgumentTypeMismatchException ignored -> GlobalErrorCode.INVALID_INPUT_VALUE;
            default -> GlobalErrorCode.BAD_REQUEST;
        };

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        log.warn("[DENY] {} {} | {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        ErrorCode errorCode = AuthErrorCode.FORBIDDEN;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("[FATAL] {} {} | Unhandled Exception",
                request.getMethod(),
                request.getRequestURI(),
                ex);

        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.onFailure(errorCode));
    }
}

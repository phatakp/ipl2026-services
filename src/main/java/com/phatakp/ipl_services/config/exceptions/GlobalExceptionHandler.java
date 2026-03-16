package com.phatakp.ipl_services.config.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<Map<String, String>> generalErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fieldErr) {
                Map<String, String> errors = new HashMap<>();
                String fieldName = fieldErr.getField();
                String errorMessage = fieldErr.getDefaultMessage();
                errors.put("field", fieldName);
                errors.put("message", errorMessage);
                generalErrors.add(errors);
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("error", error.getDefaultMessage());
                generalErrors.add(errors);
            }
        });
        HttpErrorResponse response = HttpErrorResponse.of("Unprocessable entity", 422,  generalErrors);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<HttpErrorResponse> handleException(APIException e) {
        log.info("Handling ApiException: {}-{}", e.getMessage(),e.getStatus());
        var response = HttpErrorResponse.of(e.getMessage(), e.getStatus(), null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponse> handleException(BadCredentialsException e) {
        log.info("Handling BadCredentialsException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), 401, null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(401));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<HttpErrorResponse> handleException(MissingRequestCookieException e) {
        log.info("Handling MissingRequestCookieException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), 401, null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(401));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AuthorizationDeniedException e) {
        log.info("Handling AuthorizationDeniedException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), 403, null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(403));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpErrorResponse> handleException(HttpMessageNotReadableException e) {
        log.info("Handling HttpMessageNotReadableException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), 400, null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(400));
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpErrorResponse> handleIOException(IOException e) {
        log.info("Handling IOException: {}", e.getMessage());
        var response = HttpErrorResponse.of(e.getMessage(), 400, null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(400));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception", e);
        var response = HttpErrorResponse.of("Unexpected error", 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

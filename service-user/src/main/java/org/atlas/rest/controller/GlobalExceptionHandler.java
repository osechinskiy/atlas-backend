package org.atlas.rest.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.atlas.exception.ChangePasswordException;
import org.atlas.exception.InvalidPasswordException;
import org.atlas.exception.UserAlreadyExistException;
import org.atlas.exception.UserNotFoundException;
import org.atlas.exception.UserPhoneNotFound;
import org.atlas.rest.dto.CustomErrorResponse;
import org.atlas.rest.exception.UserUnAuthorized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<CustomErrorResponse> catchUserAlreadyExistException(UserAlreadyExistException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> catchUserNotFoundException(UserNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChangePasswordException.class)
    public ResponseEntity<CustomErrorResponse> catchChangePasswordException(ChangePasswordException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserPhoneNotFound.class)
    public ResponseEntity<CustomErrorResponse> catchUserPhoneNotFoundException(UserPhoneNotFound ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnAuthorized.class)
    public ResponseEntity<CustomErrorResponse> catchUserNotFoundException(UserUnAuthorized ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<CustomErrorResponse> catchInvalidPasswordException(InvalidPasswordException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(
                new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка на сервере."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package org.atlas.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.atlas.exception.InvalidTypeOfPerformedException;
import org.atlas.exception.TypeOfPerformedNotFoundException;
import org.atlas.rest.dto.CustomErrorResponse;
import org.atlas.rest.exception.UserUnAuthorized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserUnAuthorized.class)
    public ResponseEntity<CustomErrorResponse> catchUserNotFoundException(UserUnAuthorized ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTypeOfPerformedException.class)
    public ResponseEntity<CustomErrorResponse> catchInvalidTypeOfPerformedException(
            InvalidTypeOfPerformedException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TypeOfPerformedNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> catchTypeOfPerformedNotFoundException(
            TypeOfPerformedNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new CustomErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(
                new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка на сервере."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

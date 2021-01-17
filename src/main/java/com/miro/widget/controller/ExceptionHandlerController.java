package com.miro.widget.controller;

import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.miro.widget.service.WidgetService;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WidgetService.WidgetNotFoundException.class)
    public ResponseEntity<Object> handleWidgetNotFoundException(
            final WidgetService.WidgetNotFoundException ex,
            final WebRequest request
    ) {
        final Map<String, Object> body = Map.of(
                "timestamp", ZonedDateTime.now(),
                "message", ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            final IllegalArgumentException ex,
            final WebRequest request
    ) {
        final Map<String, Object> body = Map.of(
                "timestamp", ZonedDateTime.now(),
                "message", ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}

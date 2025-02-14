package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.exceptions.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(404)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(409)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnavailableItemBookingException(UnavailableItemBookingException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateException(InvalidDateException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        return ErrorResponse.builder()
                .error(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAuthorizationException(AuthorizationException e) {
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(403)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        System.out.println("Class: " + e.getClass());
        System.out.println("Message: " + e.getMessage());
        System.out.println("Cause: " + e.getCause());
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(500)
                .build();
    }
}

package ru.practicum.shareit.exception.exceptions;

public class UncompletedBookingCommentException extends RuntimeException {
    public UncompletedBookingCommentException(String message) {
        super(message);
    }
}

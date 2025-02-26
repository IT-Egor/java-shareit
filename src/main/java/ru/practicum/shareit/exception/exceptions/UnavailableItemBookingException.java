package ru.practicum.shareit.exception.exceptions;

public class UnavailableItemBookingException extends RuntimeException {
    public UnavailableItemBookingException(String message) {
        super(message);
    }
}

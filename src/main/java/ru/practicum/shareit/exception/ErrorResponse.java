package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final LocalDateTime timestamp;
    private final int status;
}

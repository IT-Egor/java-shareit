package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Available is required")
    private Boolean available;

    @Getter(AccessLevel.NONE)
    private Long requestId;

    public Optional<Long> getRequestId() {
        return Optional.ofNullable(requestId);
    }
}

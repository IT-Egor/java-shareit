package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestResponseJsonTest {

    @Autowired
    private JacksonTester<RequestResponse> jsonTester;

    @Test
    void testRequestResponseSerialization() throws Exception {
        LocalDateTime createdAt = LocalDateTime.of(2023, 10, 1, 12, 0);

        RequestResponse response = RequestResponse.builder()
                .id(1L)
                .description("Test Description")
                .requesterId(123L)
                .created(createdAt)
                .build();

        JsonContent<RequestResponse> json = jsonTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(json).extractingJsonPathNumberValue("$.requesterId").isEqualTo(123);
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T12:00:00");
    }

    @Test
    void testRequestResponseDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Test Description\",\"requesterId\":123,\"created\":\"2023-10-01T12:00:00\"}";

        RequestResponse response = jsonTester.parseObject(jsonContent);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getRequesterId()).isEqualTo(123L);
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 12, 0));
    }
}
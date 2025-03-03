package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestCreateRequestJsonTest {

    @Autowired
    private JacksonTester<RequestCreateRequest> jsonTester;

    @Test
    void testRequestCreateRequestSerialization() throws Exception {
        RequestCreateRequest request = RequestCreateRequest.builder()
                .description("Test Description")
                .build();

        JsonContent<RequestCreateRequest> json = jsonTester.write(request);

        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
    }

    @Test
    void testRequestCreateRequestDeserialization() throws Exception {
        String jsonContent = "{\"description\":\"Test Description\"}";

        RequestCreateRequest request = jsonTester.parseObject(jsonContent);

        assertThat(request.getDescription()).isEqualTo("Test Description");
    }
}
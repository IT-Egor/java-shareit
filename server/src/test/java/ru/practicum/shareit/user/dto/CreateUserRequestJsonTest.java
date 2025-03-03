package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateUserRequestJsonTest {

    @Autowired
    private JacksonTester<CreateUserRequest> createUserRequestJson;

    @Test
    void testCreateUserRequestSerialization() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        JsonContent<CreateUserRequest> json = createUserRequestJson.write(createUserRequest);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testCreateUserRequestDeserialization() throws Exception {
        String jsonContent = "{\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        CreateUserRequest createUserRequest = createUserRequestJson.parseObject(jsonContent);

        assertThat(createUserRequest.getName()).isEqualTo("Test User");
        assertThat(createUserRequest.getEmail()).isEqualTo("test@example.com");
    }
}
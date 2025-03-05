package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserResponseJsonTest {

    @Autowired
    private JacksonTester<UserResponse> userResponseJson;

    @Test
    void testUserResponseSerialization() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        JsonContent<UserResponse> json = userResponseJson.write(userResponse);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testUserResponseDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserResponse userResponse = userResponseJson.parseObject(jsonContent);

        assertThat(userResponse.getId()).isEqualTo(1L);
        assertThat(userResponse.getName()).isEqualTo("Test User");
        assertThat(userResponse.getEmail()).isEqualTo("test@example.com");
    }
}
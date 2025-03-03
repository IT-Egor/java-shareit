package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UpdateUserRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> updateUserRequestJson;

    @Test
    void testUpdateUserRequestSerialization() throws Exception {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .name("Updated Test User")
                .email("updated_test@example.com")
                .build();

        JsonContent<UpdateUserRequest> json = updateUserRequestJson.write(updateUserRequest);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Updated Test User");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("updated_test@example.com");
    }

    @Test
    void testUpdateUserRequestDeserialization() throws Exception {
        String jsonContent = "{\"name\":\"Updated Test User\",\"email\":\"updated_test@example.com\"}";

        UpdateUserRequest updateUserRequest = updateUserRequestJson.parseObject(jsonContent);

        assertThat(updateUserRequest.getName()).isEqualTo("Updated Test User");
        assertThat(updateUserRequest.getEmail()).isEqualTo("updated_test@example.com");
    }
}
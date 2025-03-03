package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCommentResponseJsonTest {

    @Autowired
    private JacksonTester<ItemCommentResponse> jsonTester;

    @Test
    public void testItemCommentResponseSerialization() throws Exception {
        LocalDateTime createdAt = LocalDateTime.of(2023, 10, 1, 12, 0);

        ItemCommentResponse response = ItemCommentResponse.builder()
                .id(1L)
                .text("Test Comment")
                .authorName("Test Author")
                .created(createdAt)
                .build();

        JsonContent<ItemCommentResponse> json = jsonTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Test Comment");
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("Test Author");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T12:00:00");
    }

    @Test
    public void testItemCommentResponseDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"Test Comment\",\"authorName\":\"Test Author\",\"created\":\"2023-10-01T12:00:00\"}";

        ItemCommentResponse response = jsonTester.parseObject(jsonContent);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getText()).isEqualTo("Test Comment");
        assertThat(response.getAuthorName()).isEqualTo("Test Author");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 12, 0));
    }
}
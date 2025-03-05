package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MergeCommentResponseJsonTest {

    @Autowired
    private JacksonTester<MergeCommentResponse> jsonTester;

    @Test
    void testMergeCommentResponseSerialization() throws Exception {
        LocalDateTime createdAt = LocalDateTime.of(2023, 10, 1, 12, 0);
        ItemResponse item = new ItemResponse(1L, "Item 1", "Description 1", true, 123L, null);

        MergeCommentResponse response = MergeCommentResponse.builder()
                .id(1L)
                .text("Test Comment")
                .item(item)
                .authorName("Test Author")
                .created(createdAt)
                .build();

        JsonContent<MergeCommentResponse> json = jsonTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Test Comment");
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("Test Author");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo("2023-10-01T12:00:00");
        assertThat(json).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.item.name").isEqualTo("Item 1");
    }

    @Test
    void testMergeCommentResponseDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"text\":\"Test Comment\",\"item\":{\"id\":1,\"name\":\"Item 1\",\"description\":\"Description 1\",\"available\":true,\"ownerId\":123},\"authorName\":\"Test Author\",\"created\":\"2023-10-01T12:00:00\"}";

        MergeCommentResponse response = jsonTester.parseObject(jsonContent);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getText()).isEqualTo("Test Comment");
        assertThat(response.getAuthorName()).isEqualTo("Test Author");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2023, 10, 1, 12, 0));
        assertThat(response.getItem().getId()).isEqualTo(1L);
        assertThat(response.getItem().getName()).isEqualTo("Item 1");
    }
}
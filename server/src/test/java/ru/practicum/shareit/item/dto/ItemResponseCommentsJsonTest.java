package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemResponseCommentsJsonTest {

    @Autowired
    private JacksonTester<ItemResponseComments> jsonTester;

    @Test
    void testItemResponseCommentsSerialization() throws Exception {
        LocalDateTime nextBooking = LocalDateTime.of(2023, 10, 2, 12, 0);
        LocalDateTime lastBooking = LocalDateTime.of(2023, 10, 1, 12, 0);
        List<ItemCommentResponse> comments = List.of(
                new ItemCommentResponse(1L, "Comment 1", "Author 1", LocalDateTime.of(2023, 10, 1, 10, 0))
        );

        ItemResponseComments response = ItemResponseComments.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .ownerId(123L)
                .nextBooking(nextBooking)
                .lastBooking(lastBooking)
                .comments(comments)
                .build();

        JsonContent<ItemResponseComments> json = jsonTester.write(response);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Item 1");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Description 1");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("$.ownerId").isEqualTo(123);
        assertThat(json).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2023-10-02T12:00:00");
        assertThat(json).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2023-10-01T12:00:00");
        assertThat(json).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(json).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Comment 1");
    }

    @Test
    void testItemResponseCommentsDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Item 1\",\"description\":\"Description 1\",\"available\":true," +
                "\"ownerId\":123,\"nextBooking\":\"2023-10-02T12:00:00\",\"lastBooking\":\"2023-10-01T12:00:00\"," +
                "\"comments\":[{\"id\":1,\"text\":\"Comment 1\",\"authorName\":\"Author 1\",\"created\":\"2023-10-01T10:00:00\"}]}";

        ItemResponseComments response = jsonTester.parseObject(jsonContent);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Item 1");
        assertThat(response.getDescription()).isEqualTo("Description 1");
        assertThat(response.getAvailable()).isEqualTo(true);
        assertThat(response.getOwnerId()).isEqualTo(123L);
        assertThat(response.getNextBooking()).isEqualTo(LocalDateTime.of(2023, 10, 2, 12, 0));
        assertThat(response.getLastBooking()).isEqualTo(LocalDateTime.of(2023, 10, 1, 12, 0));
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getComments().get(0).getId()).isEqualTo(1L);
        assertThat(response.getComments().get(0).getText()).isEqualTo("Comment 1");
    }
}
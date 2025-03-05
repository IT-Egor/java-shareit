package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CommentTest {

    @Test
    void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();
        comment3.setId(2L);

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
    }

    @Test
    void testHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}


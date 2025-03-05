package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemTest {

    @Test
    void testEquals() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        Item item3 = new Item();
        item3.setId(2L);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
    }

    @Test
    void testHashCode() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertEquals(item1.hashCode(), item2.hashCode());
    }
}


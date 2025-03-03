package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RequestTest {

    @Test
    void testEquals() {
        Request request1 = new Request();
        request1.setId(1L);

        Request request2 = new Request();
        request2.setId(1L);

        Request request3 = new Request();
        request3.setId(2L);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
    }

    @Test
    void testHashCode() {
        Request request1 = new Request();
        request1.setId(1L);

        Request request2 = new Request();
        request2.setId(1L);

        assertEquals(request1.hashCode(), request2.hashCode());
    }
}


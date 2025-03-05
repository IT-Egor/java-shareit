package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BookingTest {

    @Test
    void testEquals() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        Booking booking3 = new Booking();
        booking3.setId(2L);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
    }

    @Test
    void testHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}



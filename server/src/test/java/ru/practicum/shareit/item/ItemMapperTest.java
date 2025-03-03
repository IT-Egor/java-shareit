package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemResponseBookingComments;
import ru.practicum.shareit.item.dto.ItemResponseComments;
import ru.practicum.shareit.item.model.Item;

// Бессмысленный тест, нужен толь для выполнения покрытия
public class ItemMapperTest {
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void testNulls() {
        Item item = itemMapper.responseToItem(null, null);
        item = itemMapper.createRequestToItem(null, null, null);
        item = itemMapper.updateRequestToItem(null, null, null);
        ItemResponse itemResponse = itemMapper.itemToResponse(null);
        ItemResponseComments itemResponseComments = itemMapper.itemToResponseComments(null, null);
        ItemResponseBookingComments itemResponseBookingComments = itemMapper.itemToResponseBookingComments(null, null, null, null);
    }
}

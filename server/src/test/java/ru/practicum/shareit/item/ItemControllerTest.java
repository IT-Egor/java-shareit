package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void testCreateItem() throws Exception {
        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        Mockito.when(itemService.createItem(Mockito.any(CreateItemRequest.class), Mockito.eq(1L))).thenReturn(itemResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testUpdateItem() throws Exception {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .name("Updated Test Item")
                .description("Updated Test Description")
                .available(false)
                .build();

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("Updated Test Item")
                .description("Updated Test Description")
                .available(false)
                .build();

        Mockito.when(itemService.updateItem(Mockito.eq(1L), Mockito.any(UpdateItemRequest.class), Mockito.eq(1L))).thenReturn(itemResponse);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Test Item"))
                .andExpect(jsonPath("$.description").value("Updated Test Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testGetItem() throws Exception {
        ItemResponseComments itemResponse = ItemResponseComments.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        Mockito.when(itemService.findItemWithComments(1L)).thenReturn(itemResponse);

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testGetAllUserItems() throws Exception {
        ItemResponseBookingComments itemResponse = ItemResponseBookingComments.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        Mockito.when(itemService.getAllUserItems(1L)).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void testSearchItems() throws Exception {
        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        Mockito.when(itemService.searchItems("test")).thenReturn(List.of(itemResponse));

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void testAddComment() throws Exception {
        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .text("Test Comment")
                .build();

        MergeCommentResponse commentResponse = MergeCommentResponse.builder()
                .id(1L)
                .text("Test Comment")
                .authorName("Test Author")
                .build();

        Mockito.when(itemService.addComment(Mockito.any(CreateCommentRequest.class), Mockito.eq(1L), Mockito.eq(1L))).thenReturn(commentResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorName").value("Test Author"));
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    void testCreateRequest() throws Exception {
        RequestCreateDto createRequest = RequestCreateDto.builder()
                .description("request description test")
                .build();

        RequestResponse requestResponse = RequestResponse.builder()
                .id(1L)
                .description("request description test")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        Mockito.when(requestService.createRequest(Mockito.any(RequestCreateDto.class), Mockito.eq(1L)))
                .thenReturn(requestResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("request description test"))
                .andExpect(jsonPath("$.requesterId").value(1L));
    }

    @Test
    void testGetUserRequests() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .available(true)
                .build();

        RequestWithAnswersResponse requestWithAnswersResponse = RequestWithAnswersResponse.builder()
                .id(1L)
                .description("request description test")
                .created(LocalDateTime.now())
                .items(Collections.singletonList(item))
                .build();

        List<RequestWithAnswersResponse> requests = Collections.singletonList(requestWithAnswersResponse);

        Mockito.when(requestService.findAllUserRequests(Mockito.eq(1L))).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("request description test"))
                .andExpect(jsonPath("$[0].items[0].id").value(1L))
                .andExpect(jsonPath("$[0].items[0].name").value("item name test"))
                .andExpect(jsonPath("$[0].items[0].description").value("item description test"));
    }

    @Test
    void testGetAllRequests() throws Exception {
        RequestResponse requestResponse = RequestResponse.builder()
                .id(1L)
                .description("request description test")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        List<RequestResponse> requests = Collections.singletonList(requestResponse);

        Mockito.when(requestService.findAllRequests()).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("request description test"))
                .andExpect(jsonPath("$[0].requesterId").value(1L));
    }

    @Test
    void testGetRequestById() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .available(true)
                .build();

        RequestWithAnswersResponse requestWithAnswersResponse = RequestWithAnswersResponse.builder()
                .id(1L)
                .description("request description test")
                .created(LocalDateTime.now())
                .items(Collections.singletonList(item))
                .build();

        Mockito.when(requestService.findRequestById(Mockito.eq(1L))).thenReturn(requestWithAnswersResponse);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("request description test"))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("item name test"))
                .andExpect(jsonPath("$.items[0].description").value("item description test"));
    }
}
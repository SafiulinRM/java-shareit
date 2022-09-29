package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;


    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "description",
            1L,
            null,
            null);


    @Test
    void saveNewRequest() throws Exception {
        when(itemRequestService.add(anyLong(), any()))
                .thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(requestDto);
        mvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));
    }

    @Test
    void getRequestsOfUser() throws Exception {
        when(itemRequestService.getRequestsOfUser(anyLong()))
                .thenReturn(List.of(requestDto));
        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyLong()))
                .thenReturn(List.of(requestDto));
        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
    }
}
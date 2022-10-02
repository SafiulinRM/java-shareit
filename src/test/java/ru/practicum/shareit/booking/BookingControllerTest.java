package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final LocalDateTime start = LocalDateTime
            .of(2024, 1, 1, 1, 1, 1);
    public static final LocalDateTime end = LocalDateTime
            .of(2025, 1, 1, 1, 1, 1);
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final BookingDtoInput bookingDtoInput =
            new BookingDtoInput(1L, start, end, 1L, 1L, State.WAITING);
    private final BookingDtoOutput.Booker bookerOut = new BookingDtoOutput.Booker(2L, "user2");
    private final BookingDtoOutput.Item itemOut = new BookingDtoOutput.Item(1L, "item");
    private final BookingDtoOutput bookingDtoOutput =
            new BookingDtoOutput(1L, start, end, itemOut, bookerOut, State.WAITING);

    @Test
    void add() throws Exception {
        when(bookingService.add(anyLong(), any()))
                .thenReturn(bookingDtoOutput);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .header(USER_ID_HEADER, 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOutput)));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDtoOutput);
        mvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOutput)));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDtoOutput);
        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOutput)));
    }

    @Test
    void getBookingsOfUser() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOutput));
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoOutput))));
    }

    @Test
    void getBookingsForAllItemsOfUser() throws Exception {
        when(bookingService.getBookingsForAllItemsOfUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOutput));
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDtoOutput))));
    }
}
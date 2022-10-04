package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoOutputJsonTest {
    @Autowired
    private JacksonTester<BookingDtoOutput> json;
    private final BookingDtoOutput.Booker bookerOut = new BookingDtoOutput.Booker(2L, "user2");
    private final BookingDtoOutput.Item itemOut = new BookingDtoOutput.Item(1L, "item");

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDtoOutput(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                itemOut,
                bookerOut,
                State.REJECTED);
        var result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");
    }
}

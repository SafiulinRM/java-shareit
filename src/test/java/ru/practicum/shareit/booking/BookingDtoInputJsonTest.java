package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoInputJsonTest {
    @Autowired
    private JacksonTester<BookingDtoInput> json;

    @Test
    void testSerialize() throws Exception {
        var dto = new BookingDtoInput(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                1L,
                2L,
                State.REJECTED);
        var result = json.write(dto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId");
        assertThat(result).extractingJsonPathStringValue("$.state").isEqualTo("REJECTED");
    }
}

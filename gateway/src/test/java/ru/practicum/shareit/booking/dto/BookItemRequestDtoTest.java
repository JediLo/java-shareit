package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> jsonTester;

    @Test
    void shouldSerializeDtoCorrect() throws Exception {


        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                LocalDateTime.of(2026, 2, 15, 6, 30),
                LocalDateTime.of(2026, 2, 15, 6, 35),
                15L
        );

        JsonContent<BookItemRequestDto> jsonContent = jsonTester.write(bookItemRequestDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-02-15T06:30:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-02-15T06:35:00");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(15);
    }

    @Test
    void shouldDeserializeDtoCorrect() throws Exception {
        String content = """
                {
                "start": "2026-02-15T06:30:00",
                "end": "2026-02-15T06:35:00",
                "itemId": "15"}
                """;
        BookItemRequestDto bookItemRequestDto = jsonTester.parse(content).getObject();
        assertThat(bookItemRequestDto.getStart())
                .isEqualTo(LocalDateTime.of(2026, 2, 15, 6, 30));
        assertThat(bookItemRequestDto.getEnd())
                .isEqualTo(LocalDateTime.of(2026, 2, 15, 6, 35));
        assertThat(bookItemRequestDto.getItemId()).isEqualTo(15L);

    }

}
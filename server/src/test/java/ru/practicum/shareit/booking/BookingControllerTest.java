package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Test
    void shouldGetBooking() throws Exception {

        Long userId = 1L;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(15L);

        when(bookingService.getBooking(eq(15L), eq(userId)))
                .thenReturn(responseDto);
        mockMvc.perform(get("/bookings/15")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).getBooking(eq(15L), eq(userId));
    }

    @Test
    void shouldGetBookingsByStateToBooker() throws Exception {

        Long userId = 1L;

        BookingResponseDto responseDtoFirst = new BookingResponseDto();
        responseDtoFirst.setId(15L);

        BookingResponseDto responseDtoSecond = new BookingResponseDto();
        responseDtoSecond.setId(16L);

        List<BookingResponseDto> responseDtoList = List.of(responseDtoFirst, responseDtoSecond);

        when(bookingService.getBookingsByStateToBooker(userId, BookingState.ALL, 0, 10))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtoFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDtoSecond.getId()));
        verify(bookingService).getBookingsByStateToBooker(userId, BookingState.ALL, 0, 10);

    }

    @Test
    void shouldGetBookingsByStateToOwnerWithoutFromAndSize() throws Exception {

        Long userId = 1L;

        BookingResponseDto responseDtoFirst = new BookingResponseDto();
        responseDtoFirst.setId(15L);

        BookingResponseDto responseDtoSecond = new BookingResponseDto();
        responseDtoSecond.setId(16L);

        List<BookingResponseDto> responseDtoList = List.of(responseDtoFirst, responseDtoSecond);

        when(bookingService.getBookingsByStateToOwner(userId, BookingState.ALL, 0, 10))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDtoFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDtoSecond.getId()));
        verify(bookingService).getBookingsByStateToOwner(userId, BookingState.ALL, 0, 10);
    }

    @Test
    void shouldCreateBooking() throws Exception {

        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(15L);
        bookingDto.setStart(now.plusMinutes(10));
        bookingDto.setEnd(now.plusMinutes(20));

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingDto.getId());
        responseDto.setStart(bookingDto.getStart());
        responseDto.setEnd(bookingDto.getEnd());

        when(bookingService.addBooking(any(BookingDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));
        verify(bookingService).addBooking(any(BookingDto.class), eq(userId));
    }

    @Test
    void shouldAddStatusBooking() throws Exception {

        Long userId = 1L;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(15L);

        when(bookingService.addStatusBooking(true, 15L, userId))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/15")
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));
        verify(bookingService).addStatusBooking(true, 15L, userId);
    }

    @Test
    void should400BadRequestWithoutHeaderToGetMapping() throws Exception {

        mockMvc.perform(get("/bookings/15"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutHeaderToPostMapping() throws Exception {

        mockMvc.perform(post("/bookings"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutHeaderToPatchMapping() throws Exception {

        mockMvc.perform(patch("/bookings/15"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutState() throws Exception {

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestIncorrectState() throws Exception {

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "INCORRECT"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);
    }

    @Test
    void should400BadRequestWithoutApproved() throws Exception {

        mockMvc.perform(patch("/bookings/15")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingService);

    }
}
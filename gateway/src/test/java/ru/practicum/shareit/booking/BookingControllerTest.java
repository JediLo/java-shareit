package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    //PostMapping

    @Test
    void shouldBookSuccessfully() throws Exception {

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusMinutes(30),
                15L
        );

        when(bookingClient.bookItem(eq(15L), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(eq(15L), any(BookItemRequestDto.class));

    }

    @Test
    void should400BadRequestWhenIncorrectValidation() throws Exception {
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                null,
                LocalDateTime.now().plusMinutes(30),
                15L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWithoutHeader() throws Exception {
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusMinutes(30),
                15L
        );
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenStartBeforeNow() throws Exception {
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(30),
                15L
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    // GetMapping("/{bookingId}")

    @Test
    void shouldGetBookByIdSuccessfully() throws Exception {


        when(bookingClient.getBookingById(eq(15L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingById(eq(15L), eq(1L));

    }

    @Test
    void should400BadRequestWhenIncorrectPathByGetById() throws Exception {

        mockMvc.perform(get("/bookings/incorrect")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByGetById() throws Exception {

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    // GetMapping("/owner")

    @Test
    void shouldGetBookingsByOwnerIdSuccessfully() throws Exception {
        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.WAITING), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 15L)
                        .param("state", "WAITING")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromOwner(eq(15L), eq(BookingState.WAITING), eq(1), eq(10));
    }

    @Test
    void shouldGetBookingsByOwnerIdSuccessfullyWithoutParam() throws Exception {
        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(0), eq(10));
    }


    @Test
    void should400BadRequestWithoutHeaderByOwner() throws Exception {

        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFromByOwner() throws Exception {

        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamStateByOwner() throws Exception {

        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "incorrect")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamSizeByOwner() throws Exception {

        when(bookingClient.getBookingsFromOwner(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    // GetMapping By Booker

    @Test
    void shouldGetBookingsByBookerIdSuccessfully() throws Exception {
        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 15L)
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10));
    }

    @Test
    void shouldGetBookingsByBookerIdSuccessfullyWithoutParam() throws Exception {
        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 15L))

                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(0), eq(10));
    }


    @Test
    void should400BadRequestWithoutHeaderByBooker() throws Exception {

        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFromByBooker() throws Exception {

        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamStateByBooker() throws Exception {

        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .param("state", "incorrect")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamSizeByBooker() throws Exception {

        when(bookingClient.getBookingsFromBooker(eq(15L), eq(BookingState.ALL), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(bookingClient);
    }

    // PatchMapping("/{bookingId}")

    @Test
    void shouldPatchBookingIdSuccessfully() throws Exception {


        when(bookingClient.updateBookingById(eq(15L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 15L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1))
                .updateBookingById(eq(15L), eq(1L), eq(true));
    }

    @Test
    void should400BadRequestWithoutParamApprovedByPatch() throws Exception {

        when(bookingClient.updateBookingById(eq(15L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByPatch() throws Exception {

        when(bookingClient.updateBookingById(eq(15L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true"))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void should400BadRequestIncorrectPathByPatch() throws Exception {

        when(bookingClient.updateBookingById(eq(15L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/bookings/incorrect")
                        .header("X-Sharer-User-Id", 15L)
                        .param("approved", "true"))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }


}
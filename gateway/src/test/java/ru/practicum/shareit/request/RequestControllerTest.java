package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    // PostMapping createRequest()

    @Test
    void shouldRequestSuccessfully() throws Exception {

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("request");

        when(requestClient.createRequest(eq(15L), any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).createRequest(eq(15L), any(ItemRequestCreateDto.class));

    }

    @Test
    void should400BadRequestWhenDescriptionBlank() throws Exception {

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(" ");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWithoutHeader() throws Exception {

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("request");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    // GetMapping getRequests()

    @Test
    void shouldGetRequestsIdSuccessfully() throws Exception {

        when(requestClient.getRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 15L)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getRequests(eq(15L), eq(1), eq(10));
    }

    @Test
    void shouldGetRequestsByIdSuccessfullyWithoutParam() throws Exception {
        when(requestClient.getRequests(eq(15L), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getRequests(eq(15L), eq(0), eq(10));
    }


    @Test
    void should400BadRequestWithoutHeaderByGetRequests() throws Exception {

        when(requestClient.getRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFromByGetRequests() throws Exception {

        when(requestClient.getRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }


    @Test
    void should400BadRequestWhenIncorrectParamSizeByGetRequests() throws Exception {

        when(requestClient.getRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(requestClient);
    }

    // GetMapping("/all")  getAllRequests()

    @Test
    void shouldGetAllRequestsIdSuccessfully() throws Exception {

        when(requestClient.getAllRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 15L)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllRequests(eq(15L), eq(1), eq(10));
    }

    @Test
    void shouldGetAllRequestsByIdSuccessfullyWithoutParam() throws Exception {
        when(requestClient.getAllRequests(eq(15L), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isOk());

        verify(requestClient, times(1))
                .getAllRequests(eq(15L), eq(0), eq(10));
    }

    @Test
    void should400BadRequestWithIncorrectPathByGetAllRequests() throws Exception {

        when(requestClient.getAllRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/incorrect")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByGetAllRequests() throws Exception {

        when(requestClient.getAllRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamFromByGetAllRequests() throws Exception {

        when(requestClient.getAllRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWhenIncorrectParamSizeByGetAllRequests() throws Exception {

        when(requestClient.getAllRequests(eq(15L), eq(1), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(requestClient);
    }

    // GetMapping("/{requestId}") getRequest()

    @Test
    void shouldGetRequestByIdSuccessfully() throws Exception {

        when(requestClient.getRequest(eq(15L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequest(eq(15L), eq(1L));
    }

    @Test
    void should400BadRequestWhenIncorrectPathByGetById() throws Exception {

        mockMvc.perform(get("/requests/incorrect")
                        .header("X-Sharer-User-Id", 15L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void should400BadRequestWithoutHeaderByGetById() throws Exception {

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(requestClient);
    }
}
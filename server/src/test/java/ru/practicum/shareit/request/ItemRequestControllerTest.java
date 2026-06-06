package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void shouldCreateNewRequest() throws Exception {

        Long userId = 1L;

        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setId(15L);

        ItemRequestResponseDto itemRequestResponse = new ItemRequestResponseDto();
        itemRequestResponse.setId(itemRequest.getId());

        when(itemRequestService.addRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(itemRequestResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()));

        verify(itemRequestService, times(1)).addRequest(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForCreateRequest() throws Exception {

        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setId(15L);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetRequestsByUser() throws Exception {

        Long userId = 1L;

        ItemRequestResponseDto itemRequestResponseFirst = new ItemRequestResponseDto();
        itemRequestResponseFirst.setId(15L);

        ItemRequestResponseDto itemRequestResponseSecond = new ItemRequestResponseDto();
        itemRequestResponseSecond.setId(16L);

        List<ItemRequestResponseDto> itemRequestResponseList = List.of(itemRequestResponseFirst, itemRequestResponseSecond);

        when(itemRequestService.findRequestsByUserId(userId, 0, 10)).thenReturn(itemRequestResponseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestResponseFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequestResponseSecond.getId()));

        verify(itemRequestService, times(1)).findRequestsByUserId(userId, 0, 10);
    }

    @Test
    void shouldGetRequestsByUserWithoutParams() throws Exception {

        Long userId = 1L;

        ItemRequestResponseDto itemRequestResponseFirst = new ItemRequestResponseDto();
        itemRequestResponseFirst.setId(15L);

        ItemRequestResponseDto itemRequestResponseSecond = new ItemRequestResponseDto();
        itemRequestResponseSecond.setId(16L);

        List<ItemRequestResponseDto> itemRequestResponseList = List.of(itemRequestResponseFirst, itemRequestResponseSecond);

        when(itemRequestService.findRequestsByUserId(userId, 0, 10)).thenReturn(itemRequestResponseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15))
                .andExpect(jsonPath("$[1].id").value(16));

        verify(itemRequestService, times(1)).findRequestsByUserId(userId, 0, 10);
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetRequests() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetAllRequestsByUser() throws Exception {

        Long userId = 1L;

        ItemRequestResponseDto itemRequestResponseFirst = new ItemRequestResponseDto();
        itemRequestResponseFirst.setId(15L);

        ItemRequestResponseDto itemRequestResponseSecond = new ItemRequestResponseDto();
        itemRequestResponseSecond.setId(16L);

        List<ItemRequestResponseDto> itemRequestResponseList = List.of(itemRequestResponseFirst, itemRequestResponseSecond);

        when(itemRequestService.findAllRequests(userId, 0, 10)).thenReturn(itemRequestResponseList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15))
                .andExpect(jsonPath("$[1].id").value(16));

        verify(itemRequestService, times(1)).findAllRequests(userId, 0, 10);
    }

    @Test
    void shouldGetAllRequestsByUserWithoutParams() throws Exception {

        Long userId = 1L;

        ItemRequestResponseDto itemRequestResponseFirst = new ItemRequestResponseDto();
        itemRequestResponseFirst.setId(15L);

        ItemRequestResponseDto itemRequestResponseSecond = new ItemRequestResponseDto();
        itemRequestResponseSecond.setId(16L);

        List<ItemRequestResponseDto> itemRequestResponseList = List.of(itemRequestResponseFirst, itemRequestResponseSecond);

        when(itemRequestService.findAllRequests(userId, 0, 10)).thenReturn(itemRequestResponseList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(15))
                .andExpect(jsonPath("$[1].id").value(16));

        verify(itemRequestService, times(1)).findAllRequests(userId, 0, 10);
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    void shouldGetRequestById() throws Exception {

        Long userId = 1L;

        ItemRequestResponseDto item = new ItemRequestResponseDto();
        item.setId(15L);

        when(itemRequestService.findRequestById(userId, item.getId())).thenReturn(item);

        mockMvc.perform(get("/requests/15")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15));

        verify(itemRequestService, times(1)).findRequestById(userId, item.getId());

    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetRequestById() throws Exception {
        mockMvc.perform(get("/requests/15"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemRequestService);
    }
}
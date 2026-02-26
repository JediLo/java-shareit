package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void shouldGetItemById() throws Exception {

        Long userId = 1L;
        ItemResponseDto item = new ItemResponseDto();
        item.setId(15L);

        when(itemService.findItemById(15L, userId)).thenReturn(item);

        mockMvc.perform(get("/items/15")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));

        verify(itemService, times(1)).findItemById(eq(15L), eq(userId));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetItemById() throws Exception {
        when(itemService.findItemById(15L, 1L)).thenReturn(new ItemResponseDto());
        mockMvc.perform(get("/items/15"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldGetUserItems() throws Exception {

        Long userId = 1L;

        ItemResponseDto itemFirst = new ItemResponseDto();
        itemFirst.setId(15L);

        ItemResponseDto itemSecond = new ItemResponseDto();
        itemSecond.setId(16L);

        List<ItemResponseDto> items = List.of(itemFirst, itemSecond);

        when(itemService.findAllItemsUser(userId, 0, 10)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(itemSecond.getId()));
        verify(itemService).findAllItemsUser(userId, 0, 10);
    }

    @Test
    void shouldGetUserItemsWithoutParam() throws Exception {
        Long userId = 1L;

        ItemResponseDto itemFirst = new ItemResponseDto();
        itemFirst.setId(15L);

        ItemResponseDto itemSecond = new ItemResponseDto();
        itemSecond.setId(16L);

        List<ItemResponseDto> items = List.of(itemFirst, itemSecond);

        when(itemService.findAllItemsUser(userId, 0, 10)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(itemSecond.getId()));
        verify(itemService).findAllItemsUser(userId, 0, 10);
    }

    @Test
    void shouldReturn400WhenHeaderMissingForGetUserItems() throws Exception {

        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void shouldSearchItemsByText() throws Exception {
        Long userId = 1L;

        ItemResponseDto itemFirst = new ItemResponseDto();
        itemFirst.setId(15L);

        ItemResponseDto itemSecond = new ItemResponseDto();
        itemSecond.setId(16L);

        List<ItemResponseDto> items = List.of(itemFirst, itemSecond);

        when(itemService.findItemByText("text", userId, 0, 10)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(itemSecond.getId()));
        verify(itemService).findItemByText("text", userId, 0, 10);
    }

    @Test
    void shouldReturn400WhenTextMissing() throws Exception {

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldSearchItemsByTextWithoutParam() throws Exception {
        Long userId = 1L;

        ItemResponseDto itemFirst = new ItemResponseDto();
        itemFirst.setId(15L);

        ItemResponseDto itemSecond = new ItemResponseDto();
        itemSecond.setId(16L);

        List<ItemResponseDto> items = List.of(itemFirst, itemSecond);

        when(itemService.findItemByText("text", userId, 0, 10)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemFirst.getId()))
                .andExpect(jsonPath("$[1].id").value(itemSecond.getId()));
        verify(itemService).findItemByText("text", userId, 0, 10);
    }

    @Test
    void shouldReturn400WhenHeaderMissingForSearch() throws Exception {

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldCreateItem() throws Exception {

        Long userId = 1L;

        ItemResponseDto item = new ItemResponseDto();
        item.setId(15L);

        ItemBaseDto itemBase = new ItemBaseDto();
        itemBase.setId(15L);

        when(itemService.addNewItem(eq(userId), any(ItemBaseDto.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemBase)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
        verify(itemService).addNewItem(eq(userId), any(ItemBaseDto.class));

    }

    @Test
    void shouldReturn400WhenHeaderMissingForCreateItem() throws Exception {

        ItemResponseDto item = new ItemResponseDto();
        item.setId(15L);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldUpdateItem() throws Exception {
        Long userId = 1L;

        ItemResponseDto item = new ItemResponseDto();
        item.setId(15L);

        ItemBaseDto itemBaseDto = new ItemBaseDto();
        itemBaseDto.setId(item.getId());

        when(itemService.updateItem(eq(userId), any(ItemBaseDto.class), eq(item.getId()))).thenReturn(item);

        mockMvc.perform(patch("/items/15")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemBaseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
        verify(itemService).updateItem(eq(userId), any(ItemBaseDto.class), eq(item.getId()));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForUpdateItem() throws Exception {
        ItemResponseDto item = new ItemResponseDto();
        item.setId(15L);

        mockMvc.perform(patch("/items/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldDeleteItem() throws Exception {
        Long userId = 1L;
        Long itemId = 15L;
        mockMvc.perform(delete("/items/15")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(itemService).deleteItem(userId, itemId);

    }

    @Test
    void shouldReturn400WhenHeaderMissingForDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/15"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }

    @Test
    void shouldAddComment() throws Exception {

        Long userId = 1L;
        Long itemId = 15L;

        CommentDto comment = new CommentDto();
        comment.setId(12L);

        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(12L);

        when(itemService.addComment(any(CommentDto.class), eq(itemId), eq(userId))).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/15/comment")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()));

        verify(itemService).addComment(any(CommentDto.class), eq(itemId), eq(userId));
    }

    @Test
    void shouldReturn400WhenHeaderMissingForAddComment() throws Exception {

        CommentDto comment = new CommentDto();
        comment.setId(12L);

        mockMvc.perform(post("/items/12/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(itemService);
    }
}
package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void shouldSaveRequestToDatabase() {

        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("DescriptionUser");

        ItemRequestResponseDto itemRequestResponseDtoSaved = itemRequestService.addRequest(savedUser.getId(), itemRequestDto);
        assertNotNull(itemRequestResponseDtoSaved.getId());
        assertNotNull(itemRequestResponseDtoSaved.getCreated());
        assertEquals(itemRequestDto.getDescription(), itemRequestResponseDtoSaved.getDescription());

        ItemRequest itemRequestFromDb = itemRequestRepository.findById(itemRequestResponseDtoSaved.getId()).orElseThrow();
        assertEquals(itemRequestDto.getDescription(), itemRequestFromDb.getDescription());
    }

    @Test
    void shouldFindRequestsByUserIdFromDatabase() {

        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequestFirst = new ItemRequest();
        itemRequestFirst.setDescription("DescriptionRequestFirst");
        itemRequestFirst.setRequestor(savedUser);

        ItemRequest itemRequestSecond = new ItemRequest();
        itemRequestSecond.setDescription("DescriptionRequestSecond");
        itemRequestSecond.setRequestor(savedUser);

        ItemRequest itemRequestFirstSaved = itemRequestRepository.save(itemRequestFirst);
        ItemRequest itemRequestSecondSaved = itemRequestRepository.save(itemRequestSecond);

        Collection<ItemRequestResponseDto> itemRequestsFromDb = itemRequestService.findRequestsByUserId(savedUser.getId(), 0, 10);
        assertNotNull(itemRequestsFromDb);
        assertEquals(2, itemRequestsFromDb.size());
        Iterator<ItemRequestResponseDto> iterator = itemRequestsFromDb.iterator();
        ItemRequestResponseDto itemRequestResponseFirstFromDb = iterator.next();
        ItemRequestResponseDto itemRequestResponseSecondFromDb = iterator.next();

        assertEquals(itemRequestSecondSaved.getId(), itemRequestResponseFirstFromDb.getId());
        assertEquals(itemRequestFirstSaved.getId(), itemRequestResponseSecondFromDb.getId());

        assertEquals(itemRequestSecond.getDescription(), itemRequestResponseFirstFromDb.getDescription());
        assertEquals(itemRequestFirst.getDescription(), itemRequestResponseSecondFromDb.getDescription());

    }

    @Test
    void shouldFindAllRequestsFromDatabase() {


        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedFirstUser = userRepository.save(user);
        user.setName("Name");
        user.setEmail("Email@email");
        User savedSecondUser = userRepository.save(user);


        ItemRequest itemRequestFirst = new ItemRequest();
        itemRequestFirst.setDescription("DescriptionRequestFirst");
        itemRequestFirst.setRequestor(savedFirstUser);

        ItemRequest itemRequestSecond = new ItemRequest();
        itemRequestSecond.setDescription("DescriptionRequestSecond");
        itemRequestSecond.setRequestor(savedSecondUser);

        ItemRequest itemRequestFirstSaved = itemRequestRepository.save(itemRequestFirst);
        ItemRequest itemRequestSecondSaved = itemRequestRepository.save(itemRequestSecond);

        Collection<ItemRequestResponseDto> itemRequestsFromDb = itemRequestService.findAllRequests(savedSecondUser.getId(), 0, 10);
        assertNotNull(itemRequestsFromDb);
        assertEquals(2, itemRequestsFromDb.size());
        Iterator<ItemRequestResponseDto> iterator = itemRequestsFromDb.iterator();
        ItemRequestResponseDto itemRequestResponseFirstFromDb = iterator.next();
        ItemRequestResponseDto itemRequestResponseSecondFromDb = iterator.next();

        assertEquals(itemRequestSecondSaved.getId(), itemRequestResponseFirstFromDb.getId());
        assertEquals(itemRequestFirstSaved.getId(), itemRequestResponseSecondFromDb.getId());

        assertEquals(itemRequestSecond.getDescription(), itemRequestResponseFirstFromDb.getDescription());
        assertEquals(itemRequestFirst.getDescription(), itemRequestResponseSecondFromDb.getDescription());
    }

    @Test
    void shouldFindRequestByIdFromDatabaseAndCheckItemRequestResponseDto() {
        User user = new User();
        user.setName("NameUser");
        user.setEmail("EmailUser@email");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("DescriptionRequest");
        itemRequest.setRequestor(savedUser);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setRequest(itemRequest);
        item.setOwner(user);
        Item itemSaved = itemRepository.save(item);


        ItemRequestResponseDto result = itemRequestService.findRequestById(user.getId(), savedItemRequest.getId());
        assertNotNull(result);
        assertEquals(savedItemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals(itemSaved.getId(), result.getItems().iterator().next().getItemId());

    }

}
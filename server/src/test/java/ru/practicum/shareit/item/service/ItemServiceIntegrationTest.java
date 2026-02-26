package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class ItemServiceIntegrationTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllItemsUserFromDataBase() {

        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User savedUser = userRepository.save(user);

        User otherUser = new User();
        otherUser.setName("OtherName");
        otherUser.setEmail("OtherEmail@email");
        User otherUserSaved = userRepository.save(otherUser);

        Item itemFirst = new Item();
        itemFirst.setName("ItemFirst");
        itemFirst.setDescription("DescriptionFirstItem");
        itemFirst.setAvailable(true);
        itemFirst.setOwner(savedUser);
        Item savedFirstItem = itemRepository.save(itemFirst);

        Item itemSecond = new Item();
        itemSecond.setName("ItemSecond");
        itemSecond.setDescription("DescriptionSecondItem");
        itemSecond.setAvailable(false);
        itemSecond.setOwner(savedUser);
        Item savedSecondItem = itemRepository.save(itemSecond);

        Item itemThird = new Item();
        itemThird.setName("ItemThird");
        itemThird.setDescription("DescriptionThirdItem");
        itemThird.setOwner(otherUserSaved);
        itemRepository.save(itemThird);

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(savedUser.getId(), 0, 10);
        assertNotNull(result);
        assertEquals(2, result.size());

        Iterator<ItemResponseDto> resultIterator = result.iterator();
        ItemResponseDto firstItemFromDb = resultIterator.next();
        ItemResponseDto secondItemFromDb = resultIterator.next();

        assertEquals(savedFirstItem.getId(), firstItemFromDb.getId());
        assertEquals(savedSecondItem.getId(), secondItemFromDb.getId());
        assertEquals(itemFirst.getName(), firstItemFromDb.getName());
        assertEquals(itemSecond.getName(), secondItemFromDb.getName());
        assertEquals(itemFirst.getDescription(), firstItemFromDb.getDescription());
        assertEquals(itemSecond.getDescription(), secondItemFromDb.getDescription());
        assertTrue(firstItemFromDb.getAvailable());
        assertFalse(secondItemFromDb.getAvailable());
    }

    @Test
    void addNewItem() {

        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User savedUser = userRepository.save(user);

        User requestUser = new User();
        requestUser.setName("RequestUser");
        requestUser.setEmail("RequestUser@email");
        User savedRequestUser = userRepository.save(requestUser);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(savedRequestUser);
        itemRequest.setDescription("DescriptionRequest");
        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);

        ItemBaseDto itemBaseDto = new ItemBaseDto();
        itemBaseDto.setName("ItemBaseDto");
        itemBaseDto.setDescription("DescriptionItemBaseDto");
        itemBaseDto.setAvailable(true);
        itemBaseDto.setRequestId(itemRequestSaved.getId());

        ItemResponseDto result = itemService.addNewItem(savedUser.getId(), itemBaseDto);
        assertNotNull(result);
        assertEquals(itemBaseDto.getName(), result.getName());
        assertEquals(itemBaseDto.getDescription(), result.getDescription());
        assertEquals(itemBaseDto.getAvailable(), result.getAvailable());
        assertEquals(itemBaseDto.getRequestId(), result.getRequestId());
    }

    @Test
    void shouldDeleteItemFromDataBaseById() {

        User user = new User();
        user.setName("Name");
        user.setEmail("Email@email");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedUser);
        Item savedItem = itemRepository.save(item);

        itemService.deleteItem(savedUser.getId(), savedItem.getId());
        Optional<Item> itemFromDb = itemRepository.findById(savedItem.getId());
        assertTrue(itemFromDb.isEmpty());
    }

    @Test
    void shouldFindItemWithBookingsAndCommentsByIdToOwnerFromDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setName("Name");
        owner.setEmail("Email@email");
        User savedOwner = userRepository.save(owner);

        User bookerUserFirst = new User();
        bookerUserFirst.setName("BookerFirstUser");
        bookerUserFirst.setEmail("BookerFirstUser@email");
        User savedBookerFirstUser = userRepository.save(bookerUserFirst);

        User bookerUserSecond = new User();
        bookerUserSecond.setName("BookerSecondUser");
        bookerUserSecond.setEmail("BookerSecondUser@email");
        User savedBookerSecondUser = userRepository.save(bookerUserSecond);


        Item item = new Item();
        item.setName("Item");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        Booking bookingFirst = new Booking();
        bookingFirst.setStatus(BookingStatus.APPROVED);
        bookingFirst.setItem(savedItem);
        bookingFirst.setStart(now.minusMinutes(2));
        bookingFirst.setEnd(now.minusMinutes(1));
        bookingFirst.setBooker(savedBookerFirstUser);
        Booking bookingFirstSaved = bookingRepository.save(bookingFirst);

        Booking bookingSecond = new Booking();
        bookingSecond.setStatus(BookingStatus.APPROVED);
        bookingSecond.setItem(savedItem);
        bookingSecond.setStart(now.minusMinutes(20));
        bookingSecond.setEnd(now.minusMinutes(10));
        bookingSecond.setBooker(savedBookerSecondUser);
        Booking bookingSecondSaved = bookingRepository.save(bookingSecond);

        Booking bookingThird = new Booking();
        bookingThird.setStatus(BookingStatus.APPROVED);
        bookingThird.setItem(savedItem);
        bookingThird.setStart(now.plusMinutes(30));
        bookingThird.setEnd(now.plusMinutes(40));
        bookingThird.setBooker(savedBookerFirstUser);
        Booking bookingThirdSaved = bookingRepository.save(bookingThird);

        Comment commentFirst = new Comment();
        commentFirst.setItem(savedItem);
        commentFirst.setText("First Comment");
        commentFirst.setAuthor(savedBookerFirstUser);
        Comment commentFirstSaved = commentRepository.save(commentFirst);

        Comment commentSecond = new Comment();
        commentSecond.setItem(savedItem);
        commentSecond.setText("Second Comment");
        commentSecond.setAuthor(savedBookerSecondUser);
        Comment commentSecondSaved = commentRepository.save(commentSecond);

        Comment commentThird = new Comment();
        commentThird.setItem(savedItem);
        commentThird.setText("Third Comment");
        commentThird.setAuthor(savedBookerFirstUser);
        Comment commentThirdSaved = commentRepository.save(commentThird);

        ItemResponseDto result = itemService.findItemById(savedItem.getId(), owner.getId());
        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());

        assertEquals(bookingFirstSaved.getStart(), result.getLastBooking().getStartTime());
        assertEquals(bookingFirstSaved.getEnd(), result.getLastBooking().getEndTime());

        assertEquals(bookingThirdSaved.getStart(), result.getNextBooking().getStartTime());
        assertEquals(bookingThirdSaved.getEnd(), result.getNextBooking().getEndTime());

        assertEquals(3, result.getComments().size());
    }

    @Test
    void shouldFindItemByTextFromDataBase() {

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("Email@email");
        User saveOwner = userRepository.save(owner);

        Item firstItem = new Item();
        firstItem.setName("ItemFirst");
        firstItem.setDescription("DescriptionItemFirst");
        firstItem.setAvailable(true);
        firstItem.setOwner(owner);
        itemRepository.save(firstItem);

        Item secondItem = new Item();
        secondItem.setName("ItemSecond");
        secondItem.setDescription("DescriptionSecond");
        secondItem.setAvailable(true);
        secondItem.setOwner(owner);
        itemRepository.save(secondItem);

        Item thirdItem = new Item();
        thirdItem.setName("Third");
        thirdItem.setDescription("DescriptionItemThird");
        thirdItem.setAvailable(true);
        thirdItem.setOwner(owner);
        itemRepository.save(thirdItem);

        Item fourthItem = new Item();
        fourthItem.setName("Fourth");
        fourthItem.setDescription("DescriptionFourth");
        fourthItem.setAvailable(true);
        fourthItem.setOwner(owner);
        itemRepository.save(fourthItem);


        Collection<ItemResponseDto> result = itemService.findItemByText("item", saveOwner.getId(), 0, 10);
        assertNotNull(result);
        assertEquals(3, result.size());

        Iterator<ItemResponseDto> iterator = result.iterator();
        ItemResponseDto first = iterator.next();
        ItemResponseDto second = iterator.next();
        ItemResponseDto third = iterator.next();

        assertEquals(first.getName(), firstItem.getName());
        assertEquals(second.getName(), secondItem.getName());
        assertEquals(third.getName(), thirdItem.getName());
        assertEquals(first.getDescription(), firstItem.getDescription());
        assertEquals(second.getDescription(), secondItem.getDescription());
        assertEquals(third.getDescription(), thirdItem.getDescription());

    }

    @Test
    void shouldUpdateItemInDataBase() {

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("Email@email");
        User saveOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        ItemBaseDto updatedItem = new ItemBaseDto();
        updatedItem.setName("UpdatedItem");
        updatedItem.setDescription("UpdatedDescription");
        updatedItem.setAvailable(false);

        itemService.updateItem(saveOwner.getId(), updatedItem, savedItem.getId());

        Item result = itemRepository.findById(savedItem.getId()).orElseThrow();
        assertEquals(updatedItem.getName(), result.getName());
        assertEquals(updatedItem.getDescription(), result.getDescription());
        assertFalse(result.isAvailable());
    }

    @Test
    void shouldAddCommentToDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("Email@email");
        User saveOwner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("EmailBooker@email");
        User savedBooker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(saveOwner);
        Item savedItem = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setItem(savedItem);
        booking.setBooker(savedBooker);
        booking.setStart(now.minusMinutes(10));
        booking.setEnd(now.minusMinutes(5));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto comment = new CommentDto();
        comment.setText("Comment");

        CommentResponseDto savedComment = itemService.addComment(comment, savedItem.getId(), savedBooker.getId());

        Comment result = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertNotNull(result);
        assertEquals(savedComment.getText(), result.getText());

    }
}
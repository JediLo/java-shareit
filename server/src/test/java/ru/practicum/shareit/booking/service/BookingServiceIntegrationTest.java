package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class BookingServiceIntegrationTest {

    @Autowired
    BookingServiceImpl bookingService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void shouldCreateBookingToDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        User savedOwnerUser = userRepository.save(ownerItem);

        User bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        User savedBookerUser = userRepository.save(bookerUser);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwnerUser);
        itemRepository.save(item);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(now.plusMinutes(10));
        bookingDto.setEnd(now.plusMinutes(20));

        BookingResponseDto bookingSaved = bookingService.addBooking(bookingDto, savedBookerUser.getId());

        Booking result = bookingRepository.findById(bookingSaved.getId()).orElseThrow();
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(item.getName(), result.getItem().getName());
        assertEquals(item.getDescription(), result.getItem().getDescription());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(savedBookerUser.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void shouldAddStatusBookingToDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        User savedOwnerUser = userRepository.save(ownerItem);

        User bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        User savedBookerUser = userRepository.save(bookerUser);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwnerUser);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusMinutes(10));
        booking.setEnd(now.plusMinutes(20));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(savedBookerUser);
        Booking bookingSaved = bookingRepository.save(booking);

        bookingService.addStatusBooking(true, bookingSaved.getId(), savedOwnerUser.getId());

        Booking result = bookingRepository.findById(bookingSaved.getId()).orElseThrow();
        assertEquals(BookingStatus.APPROVED, result.getStatus());

    }

    @Test
    void shouldGetBookingFromDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        User savedOwnerUser = userRepository.save(ownerItem);

        User bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        User savedBookerUser = userRepository.save(bookerUser);

        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwnerUser);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStart(now.plusMinutes(10));
        booking.setEnd(now.plusMinutes(20));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(savedBookerUser);
        Booking bookingSaved = bookingRepository.save(booking);

        BookingResponseDto result = bookingService.getBooking(bookingSaved.getId(), savedOwnerUser.getId());
        assertNotNull(result);
        assertEquals(bookingSaved.getId(), result.getId());
        assertEquals(bookingSaved.getStart(), result.getStart());
        assertEquals(bookingSaved.getEnd(), result.getEnd());
        assertEquals(savedBookerUser.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void shouldGetBookingsByStateToBookerFromDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        User savedOwnerUser = userRepository.save(ownerItem);

        User bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        User savedBookerUser = userRepository.save(bookerUser);

        User userOther = new User();
        userOther.setName("NameOther");
        userOther.setEmail("NameOther@email");
        User savedOtherUser = userRepository.save(userOther);


        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwnerUser);
        itemRepository.save(item);

        Booking bookingFirst = new Booking();
        bookingFirst.setStart(now.plusMinutes(10));
        bookingFirst.setEnd(now.plusMinutes(20));
        bookingFirst.setStatus(BookingStatus.WAITING);
        bookingFirst.setItem(item);
        bookingFirst.setBooker(savedBookerUser);
        Booking savedFirstBooking = bookingRepository.save(bookingFirst);

        Booking bookingSecond = new Booking();
        bookingSecond.setStart(now.plusMinutes(30));
        bookingSecond.setEnd(now.plusMinutes(40));
        bookingSecond.setStatus(BookingStatus.REJECTED);
        bookingSecond.setItem(item);
        bookingSecond.setBooker(savedBookerUser);
        Booking savedSecondBooking = bookingRepository.save(bookingSecond);

        Booking bookingThird = new Booking();
        bookingThird.setStart(now.plusMinutes(50));
        bookingThird.setEnd(now.plusMinutes(60));
        bookingThird.setStatus(BookingStatus.WAITING);
        bookingThird.setItem(item);
        bookingThird.setBooker(savedBookerUser);
        Booking savedThirdBooking = bookingRepository.save(bookingThird);

        Booking bookingFourth = new Booking();
        bookingFourth.setStart(now.plusMinutes(10));
        bookingFourth.setEnd(now.plusMinutes(20));
        bookingFourth.setStatus(BookingStatus.WAITING);
        bookingFourth.setItem(item);
        bookingFourth.setBooker(savedOtherUser);
        Booking savedFourthBooking = bookingRepository.save(bookingFourth);

        Collection<BookingResponseDto> result = bookingService
                .getBookingsByStateToBooker(savedBookerUser.getId(), BookingState.WAITING, 0, 10);
        assertNotNull(result);
        assertEquals(2, result.size());

        Iterator<BookingResponseDto> iterator = result.iterator();
        BookingResponseDto first = iterator.next();
        BookingResponseDto second = iterator.next();

        assertEquals(savedThirdBooking.getId(), first.getId());
        assertEquals(savedFirstBooking.getId(), second.getId());
    }

    @Test
    void shouldGetBookingsByStateToOwnerFromDataBase() {

        LocalDateTime now = LocalDateTime.now();

        User ownerItem = new User();
        ownerItem.setName("NameUser");
        ownerItem.setEmail("EmailUser@email");
        User savedOwnerUser = userRepository.save(ownerItem);

        User bookerUser = new User();
        bookerUser.setName("NameBooker");
        bookerUser.setEmail("EmailBooker@email");
        User savedBookerUser = userRepository.save(bookerUser);

        User userOther = new User();
        userOther.setName("NameOther");
        userOther.setEmail("NameOther@email");
        User savedOtherUser = userRepository.save(userOther);


        Item item = new Item();
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(savedOwnerUser);
        itemRepository.save(item);

        Booking bookingFirst = new Booking();
        bookingFirst.setStart(now.plusMinutes(10));
        bookingFirst.setEnd(now.plusMinutes(20));
        bookingFirst.setStatus(BookingStatus.WAITING);
        bookingFirst.setItem(item);
        bookingFirst.setBooker(savedBookerUser);
        Booking savedFirstBooking = bookingRepository.save(bookingFirst);

        Booking bookingSecond = new Booking();
        bookingSecond.setStart(now.plusMinutes(30));
        bookingSecond.setEnd(now.plusMinutes(40));
        bookingSecond.setStatus(BookingStatus.REJECTED);
        bookingSecond.setItem(item);
        bookingSecond.setBooker(savedBookerUser);
        Booking savedSecondBooking = bookingRepository.save(bookingSecond);

        Booking bookingThird = new Booking();
        bookingThird.setStart(now.plusMinutes(50));
        bookingThird.setEnd(now.plusMinutes(60));
        bookingThird.setStatus(BookingStatus.WAITING);
        bookingThird.setItem(item);
        bookingThird.setBooker(savedBookerUser);
        Booking savedThirdBooking = bookingRepository.save(bookingThird);

        Booking bookingFourth = new Booking();
        bookingFourth.setStart(now.plusMinutes(65));
        bookingFourth.setEnd(now.plusMinutes(70));
        bookingFourth.setStatus(BookingStatus.WAITING);
        bookingFourth.setItem(item);
        bookingFourth.setBooker(savedOtherUser);
        Booking savedFourthBooking = bookingRepository.save(bookingFourth);

        Collection<BookingResponseDto> result = bookingService
                .getBookingsByStateToOwner(savedOwnerUser.getId(), BookingState.WAITING, 0, 10);
        assertNotNull(result);
        assertEquals(3, result.size());

        Iterator<BookingResponseDto> iterator = result.iterator();
        BookingResponseDto first = iterator.next();
        BookingResponseDto second = iterator.next();
        BookingResponseDto third = iterator.next();

        assertEquals(savedFourthBooking.getId(), first.getId());
        assertEquals(savedThirdBooking.getId(), second.getId());
        assertEquals(savedFirstBooking.getId(), third.getId());
    }
}
package ru.ameeleon.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ameeleon.booking.dto.BookingRequest;
import ru.ameeleon.booking.entity.Booking;
import ru.ameeleon.booking.entity.Booking.Status;
import ru.ameeleon.booking.entity.User;
import ru.ameeleon.booking.security.JwtAuthenticationFilter;
import ru.ameeleon.booking.security.JwtUtil;
import ru.ameeleon.booking.service.BookingService;
import ru.ameeleon.booking.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JwtUtil jwtUtil;

  @MockBean
  private BookingService bookingService;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  @WithMockUser(username = "testuser")
  void getAllBookings_ShouldReturnListOfBookings() throws Exception {
    Booking booking1 = createBooking(1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), Status.CONFIRMED);
    Booking booking2 = createBooking(2L, 2L, 2L, 2L, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), Status.PENDING);
    List<Booking> bookings = Arrays.asList(booking1, booking2);

    when(bookingService.getAllBookings()).thenReturn(bookings);

    mockMvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].userId").value(2))
        .andExpect(jsonPath("$[1].status").value("PENDING"));

    verify(bookingService).getAllBookings();
  }

  @Test
  @WithMockUser(username = "testuser")
  void getBookingById_ShouldReturnBooking_WhenBookingExists() throws Exception {
    Booking booking = createBooking(1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), Status.CONFIRMED);

    when(bookingService.getBookingById(1L)).thenReturn(booking);

    mockMvc.perform(get("/api/bookings/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.hotelId").value(1))
        .andExpect(jsonPath("$.roomId").value(1))
        .andExpect(jsonPath("$.status").value("CONFIRMED"));

    verify(bookingService).getBookingById(1L);
  }

  @Test
  @WithMockUser(username = "testuser")
  void getMyBookings_ShouldReturnUserBookings() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    Booking booking1 = createBooking(1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), Status.CONFIRMED);
    Booking booking2 = createBooking(2L, 1L, 2L, 2L, LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), Status.PENDING);
    List<Booking> bookings = Arrays.asList(booking1, booking2);

    when(userService.getUserByUsername("testuser")).thenReturn(user);
    when(bookingService.getBookingsByUserId(1L)).thenReturn(bookings);

    mockMvc.perform(get("/api/bookings/my"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].userId").value(1));

    verify(userService).getUserByUsername("testuser");
    verify(bookingService).getBookingsByUserId(1L);
  }

  @Test
  @WithMockUser(username = "testuser")
  void createBooking_ShouldReturnCreatedBooking() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    BookingRequest request = new BookingRequest();
    request.setHotelId(1L);
    request.setRoomId(1L);
    request.setStartDate(LocalDate.now());
    request.setEndDate(LocalDate.now().plusDays(2));

    Booking booking = createBooking(1L, 1L, 1L, 1L, LocalDate.now(), LocalDate.now().plusDays(2), Status.PENDING);

    when(userService.getUserByUsername("testuser")).thenReturn(user);
    when(bookingService.createBooking(any(BookingRequest.class), eq(1L))).thenReturn(booking);

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.hotelId").value(1))
        .andExpect(jsonPath("$.roomId").value(1))
        .andExpect(jsonPath("$.status").value("PENDING"));

    verify(userService).getUserByUsername("testuser");
    verify(bookingService).createBooking(any(BookingRequest.class), eq(1L));
  }

  @Test
  @WithMockUser(username = "testuser")
  void cancelBooking_ShouldReturnOk_WhenBookingCancelled() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    when(userService.getUserByUsername("testuser")).thenReturn(user);
    doNothing().when(bookingService).cancelBooking(1L, 1L);

    mockMvc.perform(post("/api/bookings/1/cancel"))
        .andExpect(status().isOk());

    verify(userService).getUserByUsername("testuser");
    verify(bookingService).cancelBooking(1L, 1L);
  }

  private Booking createBooking(Long id, Long userId, Long hotelId, Long roomId, 
                   LocalDate startDate, LocalDate endDate, Status status) {
    Booking booking = new Booking();
    booking.setId(id);
    booking.setUserId(userId);
    booking.setHotelId(hotelId);
    booking.setRoomId(roomId);
    booking.setStartDate(startDate);
    booking.setEndDate(endDate);
    booking.setStatus(status);
    booking.setCreatedAt(LocalDateTime.now());
    return booking;
  }
}
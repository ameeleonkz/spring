package ru.ameeleon.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.booking.dto.BookingDto;
import ru.ameeleon.booking.dto.BookingRequest;
import ru.ameeleon.booking.entity.Booking;
import ru.ameeleon.booking.service.BookingService;
import ru.ameeleon.booking.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
  private final BookingService bookingService;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<BookingDto>> getAllBookings() {
    List<Booking> bookings = bookingService.getAllBookings();
    List<BookingDto> dtos = bookings.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
    Booking booking = bookingService.getBookingById(id);
    return ResponseEntity.ok(convertToDto(booking));
  }

  @GetMapping("/my")
  public ResponseEntity<List<BookingDto>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userService.getUserByUsername(userDetails.getUsername()).getId();
    List<Booking> bookings = bookingService.getBookingsByUserId(userId);
    List<BookingDto> dtos = bookings.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }

  @PostMapping
  public ResponseEntity<BookingDto> createBooking(
      @RequestBody BookingRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userService.getUserByUsername(userDetails.getUsername()).getId();
    Booking booking = bookingService.createBooking(request, userId);
    return ResponseEntity.ok(convertToDto(booking));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<Void> cancelBooking(
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = userService.getUserByUsername(userDetails.getUsername()).getId();
    bookingService.cancelBooking(id, userId);
    return ResponseEntity.ok().build();
  }

  private BookingDto convertToDto(Booking booking) {
    BookingDto dto = new BookingDto();
    dto.setId(booking.getId());
    dto.setUserId(booking.getUserId());
    dto.setHotelId(booking.getHotelId());
    dto.setRoomId(booking.getRoomId());
    dto.setStartDate(booking.getStartDate());
    dto.setEndDate(booking.getEndDate());
    dto.setStatus(booking.getStatus().name());
    dto.setCreatedAt(booking.getCreatedAt());
    return dto;
  }
}
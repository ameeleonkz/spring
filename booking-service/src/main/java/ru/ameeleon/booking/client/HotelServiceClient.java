package ru.ameeleon.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.booking.dto.RoomDto;
import ru.ameeleon.booking.dto.RoomReservationRequest;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "hotel-service")
public interface HotelServiceClient {

  @PostMapping("/api/reservations/reserve")
  void reserveRoom(@RequestBody RoomReservationRequest request);

  @PostMapping("/api/reservations/confirm")
  void confirmReservation(@RequestBody RoomReservationRequest request);

  @PostMapping("/api/reservations/cancel")
  void cancelReservation(@RequestParam Long roomId,
                         @RequestParam Long bookingId);

  @GetMapping("/api/rooms/recommend")
  List<RoomDto> getRecommendedRooms(@RequestParam Long hotelId,
                                    @RequestParam LocalDate startDate,
                                    @RequestParam LocalDate endDate);

  @PostMapping("/api/rooms/{id}/increment-bookings")
  void incrementTimesBooked(@PathVariable Long id);
}
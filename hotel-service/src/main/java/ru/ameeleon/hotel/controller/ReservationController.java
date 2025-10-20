package ru.ameeleon.hotel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.hotel.dto.RoomReservationRequest;
import ru.ameeleon.hotel.service.HotelService;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final HotelService hotelService;

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveRoom(@Valid @RequestBody RoomReservationRequest request) {
        hotelService.reserveRoom(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReservation(@Valid @RequestBody RoomReservationRequest request) {
        hotelService.confirmReservation(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelReservation(
            @RequestParam Long roomId,
            @RequestParam Long bookingId) {
        hotelService.cancelReservation(roomId, bookingId);
        return ResponseEntity.ok().build();
    }
}
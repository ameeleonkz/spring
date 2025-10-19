package ru.ameeleon.hotel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.service.RoomService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
  private final RoomService roomService;

  @GetMapping
  public ResponseEntity<List<Room>> getAllRooms() {
    return ResponseEntity.ok(roomService.getAllRooms());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
    return ResponseEntity.ok(roomService.getRoomById(id));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Room> createRoom(@RequestBody Room room) {
    return ResponseEntity.ok(roomService.createRoom(room));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
    return ResponseEntity.ok(roomService.updateRoom(id, room));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
    roomService.deleteRoom(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/available")
  public ResponseEntity<List<Room>> getAvailableRooms(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(roomService.findAvailableRooms(startDate, endDate));
  }

  @GetMapping("/recommend")
  public ResponseEntity<List<Room>> getRecommendedRooms(
      @RequestParam Long hotelId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(roomService.findRecommendedRooms(hotelId, startDate, endDate));
  }

  @PostMapping("/{id}/increment-bookings")
  public ResponseEntity<Void> incrementTimesBooked(@PathVariable Long id) {
    roomService.incrementTimesBooked(id);
    return ResponseEntity.ok().build();
  }
}
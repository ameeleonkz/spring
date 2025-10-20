package ru.ameeleon.hotel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ameeleon.hotel.dto.RoomDto;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.service.HotelService;
import ru.ameeleon.hotel.service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final HotelService hotelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        Hotel hotel = hotelService.getHotelById(roomDto.getHotelId());
        Room room = convertToEntity(roomDto);
        room.setHotel(hotel);
        Room created = roomService.createRoom(room);
        return ResponseEntity.ok(convertToDto(created));
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(convertToDto(room));
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Room> rooms;
        if (hotelId != null) {
            rooms = roomService.getAvailableRoomsByHotelId(hotelId, startDate, endDate);
        } else {
            rooms = roomService.getAvailableRooms(startDate, endDate);
        }
        
        return ResponseEntity.ok(rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<RoomDto>> getRecommendedRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Room> rooms;
        if (hotelId != null) {
            rooms = roomService.getRecommendedRoomsByHotelId(hotelId, startDate, endDate);
        } else {
            rooms = roomService.getRecommendedRooms(startDate, endDate);
        }
        
        return ResponseEntity.ok(rooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Hotel hotel = hotelService.getHotelById(roomDto.getHotelId());
        Room roomDetails = convertToEntity(roomDto);
        roomDetails.setHotel(hotel);
        Room updated = roomService.updateRoom(id, roomDetails);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }

    private RoomDto convertToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setNumber(room.getNumber());
        dto.setAvailable(room.getAvailable());
        dto.setTimesBooked(room.getTimesBooked());
        dto.setPrice(room.getPrice());
        return dto;
    }

    private Room convertToEntity(RoomDto dto) {
        Room room = new Room();
        room.setId(dto.getId());
        room.setNumber(dto.getNumber());
        room.setAvailable(dto.getAvailable());
        room.setTimesBooked(dto.getTimesBooked() != null ? dto.getTimesBooked() : 0);
        room.setPrice(dto.getPrice());
        return room;
    }
}
package ru.ameeleon.hotel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
  private final RoomRepository roomRepository;

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public Room getRoomById(Long id) {
    return roomRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Room not found"));
  }

  public Room createRoom(Room room) {
    return roomRepository.save(room);
  }

  public Room updateRoom(Long id, Room roomDetails) {
    Room room = getRoomById(id);
    room.setHotelId(roomDetails.getHotelId());
    room.setNumber(roomDetails.getNumber());
    room.setType(roomDetails.getType());
    room.setPrice(roomDetails.getPrice());
    room.setAvailable(roomDetails.getAvailable());
    return roomRepository.save(room);
  }

  public void deleteRoom(Long id) {
    roomRepository.deleteById(id);
  }

  public List<Room> findAvailableRooms(LocalDate startDate, LocalDate endDate) {
    return roomRepository.findAvailableRooms(startDate, endDate);
  }

  public List<Room> findRecommendedRooms(Long hotelId, LocalDate startDate, LocalDate endDate) {
    return roomRepository.findRecommendedRooms(hotelId, startDate, endDate);
  }

  @Transactional
  public void incrementTimesBooked(Long roomId) {
    roomRepository.incrementTimesBooked(roomId);
  }
}
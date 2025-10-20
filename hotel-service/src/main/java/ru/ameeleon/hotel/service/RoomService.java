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

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        room.setHotel(roomDetails.getHotel());
        room.setNumber(roomDetails.getNumber());
        room.setAvailable(roomDetails.getAvailable());
        room.setPrice(roomDetails.getPrice());

        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate) {
        return roomRepository.findAllAvailableRooms(startDate, endDate);
    }

    public List<Room> getRecommendedRooms(LocalDate startDate, LocalDate endDate) {
        return roomRepository.findAllRecommendedRooms(startDate, endDate);
    }

    @Transactional
    public void incrementTimesBooked(Long roomId) {
        Room room = getRoomById(roomId);
        room.setTimesBooked(room.getTimesBooked() + 1);
        roomRepository.save(room);
    }

    public List<Room> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public List<Room> getAvailableRoomsByHotelId(Long hotelId, LocalDate startDate, LocalDate endDate) {
        return roomRepository.findAvailableRooms(hotelId, startDate, endDate);
    }

    public List<Room> getRecommendedRoomsByHotelId(Long hotelId, LocalDate startDate, LocalDate endDate) {
        return roomRepository.findRecommendedRooms(hotelId, startDate, endDate);
    }
}
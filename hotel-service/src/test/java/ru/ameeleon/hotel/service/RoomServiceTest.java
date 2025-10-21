package ru.ameeleon.hotel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        
        room = new Room();
        room.setId(1L);
        room.setNumber("101");
        room.setAvailable(true);
        room.setPrice(new BigDecimal("100.0"));
        room.setHotel(hotel);
    }

    @Test
    void createRoom_ShouldSaveAndReturnRoom() {
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        
        Room result = roomService.createRoom(room);
        
        assertNotNull(result);
        assertEquals("101", result.getNumber());
        assertEquals(new BigDecimal("100.0"), result.getPrice());
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void getAllRooms_ShouldReturnRoomList() {
        Room room2 = new Room();
        room2.setId(2L);
        room2.setNumber("102");
        room2.setPrice(new BigDecimal("150.0"));
        
        when(roomRepository.findAll()).thenReturn(Arrays.asList(room, room2));
        
        List<Room> rooms = roomService.getAllRooms();
        
        assertNotNull(rooms);
        assertEquals(2, rooms.size());
        assertEquals("101", rooms.get(0).getNumber());
        assertEquals("102", rooms.get(1).getNumber());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getRoomById_ShouldReturnRoom_WhenExists() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        
        Optional<Room> result = Optional.ofNullable(roomService.getRoomById(1L));
        
        assertTrue(result.isPresent());
        assertEquals("101", result.get().getNumber());
        verify(roomRepository, times(1)).findById(1L);
    }

    @Test
    void getRoomById_ShouldThrowException_WhenNotExists() {
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomService.getRoomById(999L);
        });
        
        assertEquals("Room not found with id: 999", exception.getMessage());
        verify(roomRepository, times(1)).findById(999L);
    }

    @Test
    void updateRoom_ShouldUpdateAndReturnRoom_WhenExists() {
        Room updatedRoom = new Room();
        updatedRoom.setNumber("102");
        updatedRoom.setPrice(new BigDecimal("200.0"));
        updatedRoom.setAvailable(false);
        
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        
        Room result = roomService.updateRoom(1L, updatedRoom);
        
        assertNotNull(result);
        assertEquals("102", result.getNumber());
        assertEquals(new BigDecimal("200.0"), result.getPrice());
        assertFalse(result.getAvailable());
        verify(roomRepository, times(1)).findById(1L);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void updateRoom_ShouldThrowException_WhenNotExists() {
        Room updatedRoom = new Room();
        updatedRoom.setNumber("102");
        
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomService.updateRoom(999L, updatedRoom);
        });
        
        assertEquals("Room not found with id: 999", exception.getMessage());
        verify(roomRepository, times(1)).findById(999L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void deleteRoom_ShouldCallRepository() {
        doNothing().when(roomRepository).deleteById(1L);
        
        roomService.deleteRoom(1L);
        
        verify(roomRepository, times(1)).deleteById(1L);
    }

    @Test
    void getRoomsByHotelId_ShouldReturnRoomsByHotel() {
        when(roomRepository.findByHotelId(1L)).thenReturn(Arrays.asList(room));
        
        List<Room> results = roomService.getRoomsByHotelId(1L);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getHotel().getId());
        verify(roomRepository, times(1)).findByHotelId(1L);
    }
}
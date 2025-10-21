package ru.ameeleon.hotel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.repository.HotelRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test Street");
    }

    @Test
    void getAllHotels_ShouldReturnHotelList() {
        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("Another Hotel");
        
        when(hotelRepository.findAll()).thenReturn(Arrays.asList(hotel, hotel2));
        
        List<Hotel> hotels = hotelService.getAllHotels();
        
        assertNotNull(hotels);
        assertEquals(2, hotels.size());
        assertEquals("Test Hotel", hotels.get(0).getName());
        assertEquals("Another Hotel", hotels.get(1).getName());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void getHotelById_ShouldReturnHotel_WhenExists() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        
        Optional<Hotel> result = Optional.ofNullable(hotelService.getHotelById(1L));
        
        assertTrue(result.isPresent());
        assertEquals("Test Hotel", result.get().getName());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void updateHotel_ShouldThrowException_WhenNotExists() {
        Hotel updatedHotel = new Hotel();
        updatedHotel.setName("Updated Hotel");
        
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            hotelService.updateHotel(999L, updatedHotel);
        });
        
        assertEquals("Hotel not found with id: 999", exception.getMessage());
        verify(hotelRepository, times(1)).findById(999L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void createHotel_ShouldSaveAndReturnHotel() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        
        Hotel result = hotelService.createHotel(hotel);
        
        assertNotNull(result);
        assertEquals("Test Hotel", result.getName());
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void updateHotel_ShouldUpdateAndReturnHotel_WhenExists() {
        Hotel updatedHotel = new Hotel();
        updatedHotel.setName("Updated Hotel");
        updatedHotel.setAddress("456 New Street");
        
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);
        
        Hotel result = hotelService.updateHotel(1L, updatedHotel);
        
        assertNotNull(result);
        assertEquals("Updated Hotel", result.getName());
        assertEquals("456 New Street", result.getAddress());
        verify(hotelRepository, times(1)).findById(1L);
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void deleteHotel_ShouldCallRepository() {
        doNothing().when(hotelRepository).deleteById(1L);
        
        hotelService.deleteHotel(1L);
        
        verify(hotelRepository, times(1)).deleteById(1L);
    }
}
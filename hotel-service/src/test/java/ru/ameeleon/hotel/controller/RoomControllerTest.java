package ru.ameeleon.hotel.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ameeleon.hotel.config.JwtAuthenticationFilter;
import ru.ameeleon.hotel.config.JwtUtil;
import ru.ameeleon.hotel.config.SecurityConfig;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.service.HotelService;
import ru.ameeleon.hotel.service.RoomService;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = RoomController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class}
    )
)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private HotelService hotelService;

    @Test
    void getAllRooms_ShouldReturnRoomList() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        
        Room room1 = new Room();
        room1.setId(1L);
        room1.setNumber("101");
        room1.setAvailable(true);
        room1.setPrice(new BigDecimal("100.0"));
        room1.setHotel(hotel);
        
        Room room2 = new Room();
        room2.setId(2L);
        room2.setNumber("102");
        room2.setAvailable(false);
        room2.setPrice(new BigDecimal("150.0"));
        room2.setHotel(hotel);
        
        when(roomService.getAllRooms()).thenReturn(Arrays.asList(room1, room2));
        
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value("101"))
                .andExpect(jsonPath("$[1].number").value("102"));
    }

    @Test
    void createRoom_ShouldReturnCreatedRoom() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        
        Room room = new Room();
        room.setId(1L);
        room.setNumber("101");
        room.setAvailable(true);
        room.setPrice(new BigDecimal("100.0"));
        room.setHotel(hotel);
        
        when(roomService.createRoom(any(Room.class))).thenReturn(room);
        
        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"number\":\"101\",\"available\":true,\"price\":100.0,\"hotelId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("101"));
    }
}
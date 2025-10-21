package ru.ameeleon.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ameeleon.hotel.config.JwtAuthenticationFilter;
import ru.ameeleon.hotel.config.SecurityConfig;
import ru.ameeleon.hotel.dto.HotelDto;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.service.HotelService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HotelController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    @Test
    void getAllHotels_ShouldReturnHotelList() throws Exception {
        Hotel hotel1 = new Hotel();
        hotel1.setName("Hotel 1");
        Hotel hotel2 = new Hotel();
        hotel2.setName("Hotel 2");
        
        when(hotelService.getAllHotels()).thenReturn(Arrays.asList(hotel1, hotel2));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(hotelService).getAllHotels();
    }

    @Test
    void getHotelById_ShouldReturnHotel() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        
        when(hotelService.getHotelById(anyLong())).thenReturn(hotel);

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Hotel"));

        verify(hotelService).getHotelById(1L);
    }

    @Test
    void createHotel_ShouldReturnCreatedHotel() throws Exception {
        Hotel hotel = new Hotel();
        hotel.setName("New Hotel");
        
        when(hotelService.createHotel(any(Hotel.class))).thenReturn(hotel);

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Hotel"));

        verify(hotelService).createHotel(any(Hotel.class));
    }

    @Test
    void updateHotel_ShouldReturnUpdatedHotel() throws Exception {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setName("Updated Hotel");
        
        Hotel hotel = new Hotel();
        hotel.setName("Updated Hotel");
        
        when(hotelService.updateHotel(anyLong(), any(Hotel.class))).thenReturn(hotel);

        mockMvc.perform(put("/api/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Hotel"));

        verify(hotelService).updateHotel(anyLong(), any(Hotel.class));
    }

    @Test
    void deleteHotel_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/hotels/1"))
                .andExpect(status().isOk());

        verify(hotelService).deleteHotel(1L);
    }
}
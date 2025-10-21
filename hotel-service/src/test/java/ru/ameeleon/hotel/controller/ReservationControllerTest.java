package ru.ameeleon.hotel.controller;

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
import ru.ameeleon.hotel.dto.RoomReservationRequest;
import ru.ameeleon.hotel.service.HotelService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ReservationController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Test
    void reserveRoom_WithValidRequest_ShouldReturnOk() throws Exception {
        String requestJson = """
            {
                "roomId": 1,
                "bookingId": 1,
                "startDate": "2024-12-20",
                "endDate": "2024-12-25"
            }
            """;

        doNothing().when(hotelService).reserveRoom(any(RoomReservationRequest.class));

        mockMvc.perform(post("/api/reservations/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        verify(hotelService).reserveRoom(any(RoomReservationRequest.class));
    }

    @Test
    void reserveRoom_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        String requestJson = """
            {
                "roomId": 1
            }
            """;
        // Отсутствуют обязательные поля: bookingId, startDate, endDate

        mockMvc.perform(post("/api/reservations/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveRoom_WithoutBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/reservations/reserve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
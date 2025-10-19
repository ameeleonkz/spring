package ru.ameeleon.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
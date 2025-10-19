package ru.ameeleon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservationRequest {
    private Long roomId;
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
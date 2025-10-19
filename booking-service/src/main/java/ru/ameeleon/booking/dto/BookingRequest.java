package ru.ameeleon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
  private Long hotelId;
  private Long roomId; // Опционально, если autoSelect = true
  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean autoSelect = false; // Автоподбор номера
  private String requestId; // Для идемпотентности
}
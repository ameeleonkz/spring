package ru.ameeleon.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
  private Long id;
  private Long hotelId;
  private String number;
  private String type;
  private BigDecimal price;
  private Boolean available;
  private Integer timesBooked;
}
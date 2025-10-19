package ru.ameeleon.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelStatisticsDto {
    private Long hotelId;
    private String hotelName;
    private Integer totalRooms;
    private Integer availableRooms;
    private Integer reservedRooms;
    private BigDecimal occupancyRate;
}
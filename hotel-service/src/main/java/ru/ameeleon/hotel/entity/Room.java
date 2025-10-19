package ru.ameeleon.hotel.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hotel_id", nullable = false)
  private Long hotelId;

  @Column(nullable = false)
  private String number;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Boolean available = true;

  @Column(name = "times_booked", nullable = false)
  private Integer timesBooked = 0;

  @Column(length = 1000)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
  private Hotel hotel;
}
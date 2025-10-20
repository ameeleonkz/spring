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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(nullable = false, unique = true)
  private String number;

  @Column(nullable = false)
  private Boolean available = true;

  @Column(name = "times_booked", nullable = false)
  private Integer timesBooked = 0;

  @Column(precision = 10, scale = 2)
  private BigDecimal price;
}
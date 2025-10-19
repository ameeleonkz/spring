package ru.ameeleon.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ameeleon.booking.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByUserId(Long userId);
  Booking findByRequestId(String requestId);
}
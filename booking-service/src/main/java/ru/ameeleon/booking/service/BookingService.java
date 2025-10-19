package ru.ameeleon.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ameeleon.booking.client.HotelServiceClient;
import ru.ameeleon.booking.dto.BookingRequest;
import ru.ameeleon.booking.dto.RoomDto;
import ru.ameeleon.booking.dto.RoomReservationRequest;
import ru.ameeleon.booking.entity.Booking;
import ru.ameeleon.booking.repository.BookingRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
  private final BookingRepository bookingRepository;
  private final HotelServiceClient hotelServiceClient;

  public List<Booking> getAllBookings() {
    return bookingRepository.findAll();
  }

  public Booking getBookingById(Long id) {
    return bookingRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Booking not found"));
  }

  public List<Booking> getBookingsByUserId(Long userId) {
    return bookingRepository.findByUserId(userId);
  }

  @Transactional
  public Booking createBooking(BookingRequest request, Long userId) {
    log.info("Creating booking for user {} with requestId {}", userId, request.getRequestId());

    // Проверка идемпотентности
    if (request.getRequestId() != null) {
      Booking existingBooking = bookingRepository.findByRequestId(request.getRequestId());
      if (existingBooking != null) {
        log.info("Duplicate request detected, returning existing booking {}", existingBooking.getId());
        return existingBooking;
      }
    }

    // Автоподбор номера
    Long roomId = request.getRoomId();
    if (Boolean.TRUE.equals(request.getAutoSelect())) {
      List<RoomDto> recommendedRooms = hotelServiceClient.getRecommendedRooms(
          request.getHotelId(),
          request.getStartDate(),
          request.getEndDate()
      );
      
      if (recommendedRooms.isEmpty()) {
        throw new RuntimeException("No available rooms found for the selected dates");
      }
      
      roomId = recommendedRooms.get(0).getId();
      log.info("Auto-selected room {} for booking", roomId);
    }

    if (roomId == null) {
      throw new RuntimeException("Room ID is required when autoSelect is false");
    }

    // Создание бронирования в статусе PENDING
    Booking booking = new Booking();
    booking.setUserId(userId);
    booking.setHotelId(request.getHotelId());
    booking.setRoomId(roomId);
    booking.setStartDate(request.getStartDate());
    booking.setEndDate(request.getEndDate());
    booking.setStatus(Booking.Status.PENDING);
    booking.setRequestId(request.getRequestId());
    booking = bookingRepository.save(booking);

    try {
      // Шаг 1: Резервирование номера
      RoomReservationRequest reservationRequest = new RoomReservationRequest(
          roomId,
          booking.getId(),
          request.getStartDate(),
          request.getEndDate()
      );
      hotelServiceClient.reserveRoom(reservationRequest);
      log.info("Room {} reserved for booking {}", roomId, booking.getId());

      // Шаг 2: Подтверждение резервации
      hotelServiceClient.confirmReservation(reservationRequest);
      log.info("Reservation confirmed for booking {}", booking.getId());

      // Инкремент счетчика бронирований
      hotelServiceClient.incrementTimesBooked(roomId);
      log.info("Incremented times_booked for room {}", roomId);

      // Обновление статуса на CONFIRMED
      booking.setStatus(Booking.Status.CONFIRMED);
      return bookingRepository.save(booking);

    } catch (Exception e) {
      log.error("Failed to complete booking {}: {}", booking.getId(), e.getMessage());
      
      // Компенсация: отмена резервации
      try {
        hotelServiceClient.cancelReservation(roomId, booking.getId());
        log.info("Reservation cancelled for booking {} due to error", booking.getId());
      } catch (Exception cancelError) {
        log.error("Failed to cancel reservation for booking {}: {}", booking.getId(), cancelError.getMessage());
      }

      // Отмена бронирования
      booking.setStatus(Booking.Status.CANCELLED);
      bookingRepository.save(booking);
      
      throw new RuntimeException("Booking failed: " + e.getMessage(), e);
    }
  }

  @Transactional
  public void cancelBooking(Long id, Long userId) {
    Booking booking = getBookingById(id);

    if (!booking.getUserId().equals(userId)) {
      throw new RuntimeException("You can only cancel your own bookings");
    }

    if (booking.getStatus() == Booking.Status.CANCELLED) {
      throw new RuntimeException("Booking already cancelled");
    }

    // Отмена резервации в hotel-service
    try {
      hotelServiceClient.cancelReservation(booking.getRoomId(), booking.getId());
    } catch (Exception e) {
      log.error("Failed to cancel reservation in hotel-service: {}", e.getMessage());
    }

    booking.setStatus(Booking.Status.CANCELLED);
    bookingRepository.save(booking);
  }
}
package ru.ameeleon.hotel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ameeleon.hotel.dto.RoomReservationRequest;
import ru.ameeleon.hotel.entity.Hotel;
import ru.ameeleon.hotel.entity.Room;
import ru.ameeleon.hotel.entity.RoomReservation;
import ru.ameeleon.hotel.repository.HotelRepository;
import ru.ameeleon.hotel.repository.RoomRepository;
import ru.ameeleon.hotel.repository.RoomReservationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {
  private final HotelRepository hotelRepository;
  private final RoomRepository roomRepository;
  private final RoomReservationRepository reservationRepository;

  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  public Hotel getHotelById(Long id) {
    return hotelRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hotel not found"));
  }

  public Hotel createHotel(Hotel hotel) {
    return hotelRepository.save(hotel);
  }

  public Hotel updateHotel(Long id, Hotel hotelDetails) {
    Hotel hotel = getHotelById(id);
    hotel.setName(hotelDetails.getName());
    hotel.setAddress(hotelDetails.getAddress());
    hotel.setCity(hotelDetails.getCity());
    hotel.setDescription(hotelDetails.getDescription());
    return hotelRepository.save(hotel);
  }

  public void deleteHotel(Long id) {
    hotelRepository.deleteById(id);
  }

  @Transactional
  public void reserveRoom(RoomReservationRequest request) {
    Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(() -> new RuntimeException("Room not found"));

    if (!room.getAvailable()) {
      throw new RuntimeException("Room is not available");
    }

    // Проверка на пересечение дат
    boolean hasConflict = reservationRepository.existsByRoomIdAndDateRange(
        request.getRoomId(),
        request.getStartDate(),
        request.getEndDate()
    );

    if (hasConflict) {
      throw new RuntimeException("Room is already booked for selected dates");
    }

    RoomReservation reservation = new RoomReservation();
    reservation.setRoom(room);
    reservation.setBookingId(request.getBookingId());
    reservation.setStartDate(request.getStartDate());
    reservation.setEndDate(request.getEndDate());
    reservation.setStatus(RoomReservation.Status.PENDING);

    reservationRepository.save(reservation);
    log.info("Room {} reserved for booking {}", request.getRoomId(), request.getBookingId());
  }

  @Transactional
  public void confirmReservation(RoomReservationRequest request) {
    RoomReservation reservation = reservationRepository
        .findByRoomIdAndBookingId(request.getRoomId(), request.getBookingId())
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservation.setStatus(RoomReservation.Status.CONFIRMED);
    reservationRepository.save(reservation);
    log.info("Reservation confirmed for booking {}", request.getBookingId());
  }

  @Transactional
  public void cancelReservation(Long roomId, Long bookingId) {
    RoomReservation reservation = reservationRepository
        .findByRoomIdAndBookingId(roomId, bookingId)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservation.setStatus(RoomReservation.Status.CANCELLED);
    reservationRepository.save(reservation);
    log.info("Reservation cancelled for booking {}", bookingId);
  }
}
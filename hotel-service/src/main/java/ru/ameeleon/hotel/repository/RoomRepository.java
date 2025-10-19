package ru.ameeleon.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ameeleon.hotel.entity.Room;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
  List<Room> findByHotelId(Long hotelId);

  @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
      "(SELECT rr.room.id FROM RoomReservation rr WHERE " +
      "(rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
      "AND r.available = true " +
      "ORDER BY r.price ASC")
  List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

  @Query("SELECT r FROM Room r WHERE r.hotelId = :hotelId AND r.id NOT IN " +
      "(SELECT rr.room.id FROM RoomReservation rr WHERE " +
      "(rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
      "AND r.available = true " +
      "ORDER BY r.timesBooked ASC, r.price ASC")
  List<Room> findRecommendedRooms(@Param("hotelId") Long hotelId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

  @Modifying
  @Query("UPDATE Room r SET r.timesBooked = r.timesBooked + 1 WHERE r.id = :roomId")
  void incrementTimesBooked(@Param("roomId") Long roomId);
}
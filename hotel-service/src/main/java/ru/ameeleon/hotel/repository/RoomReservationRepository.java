package ru.ameeleon.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ameeleon.hotel.entity.RoomReservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {

    @Query("SELECT rr FROM RoomReservation rr WHERE rr.room.id = :roomId")
    List<RoomReservation> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT CASE WHEN COUNT(rr) > 0 THEN true ELSE false END FROM RoomReservation rr " +
            "WHERE rr.room.id = :roomId AND rr.status != 'CANCELLED' " +
            "AND (rr.startDate <= :endDate AND rr.endDate >= :startDate)")
    boolean existsByRoomIdAndDateRange(@Param("roomId") Long roomId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT rr FROM RoomReservation rr WHERE rr.room.id = :roomId AND rr.bookingId = :bookingId")
    Optional<RoomReservation> findByRoomIdAndBookingId(@Param("roomId") Long roomId,
                                                       @Param("bookingId") Long bookingId);
}
package ru.ameeleon.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ameeleon.hotel.entity.Room;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId")
    List<Room> findByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
           "AND r.id NOT IN (SELECT rr.room.id FROM RoomReservation rr " +
           "WHERE rr.status != 'CANCELLED' " +
           "AND (rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
           "AND r.available = true " +
           "ORDER BY r.timesBooked ASC, r.price ASC")
    List<Room> findRecommendedRooms(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
           "AND r.id NOT IN (SELECT rr.room.id FROM RoomReservation rr " +
           "WHERE rr.status != 'CANCELLED' " +
           "AND (rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
           "AND r.available = true")
    List<Room> findAvailableRooms(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Room r " +
           "WHERE r.id NOT IN (SELECT rr.room.id FROM RoomReservation rr " +
           "WHERE rr.status != 'CANCELLED' " +
           "AND (rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
           "AND r.available = true " +
           "ORDER BY r.timesBooked ASC")
    List<Room> findAllRecommendedRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Room r " +
           "WHERE r.id NOT IN (SELECT rr.room.id FROM RoomReservation rr " +
           "WHERE rr.status != 'CANCELLED' " +
           "AND (rr.startDate <= :endDate AND rr.endDate >= :startDate)) " +
           "AND r.available = true")
    List<Room> findAllAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
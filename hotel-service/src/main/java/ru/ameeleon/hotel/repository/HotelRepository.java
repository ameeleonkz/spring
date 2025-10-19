package ru.ameeleon.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ameeleon.hotel.entity.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
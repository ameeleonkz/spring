package ru.ameeleon.booking.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import ru.ameeleon.booking.dto.RoomReservationRequest;
import java.time.LocalDate;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
  "spring.cloud.openfeign.client.config.hotel-service.url=http://localhost:${wiremock.server.port}",
  "hotel-service.ribbon.listOfServers=localhost:${wiremock.server.port}"
})
class HotelServiceClientTest {

  @Autowired
  private HotelServiceClient hotelServiceClient;

  private RoomReservationRequest reservationRequest;

  @BeforeEach
  void setUp() {
    resetAllRequests();
    reservationRequest = new RoomReservationRequest();
    reservationRequest.setRoomId(1L);
    reservationRequest.setBookingId(100L);
    reservationRequest.setCheckInDate(LocalDate.of(2024, 6, 1));
    reservationRequest.setCheckOutDate(LocalDate.of(2024, 6, 5));
  }

  @Test
  void reserveRoom_ShouldCallReserveEndpoint() {
    stubFor(post(urlEqualTo("/api/reservations/reserve"))
        .withRequestBody(matchingJsonPath("$.roomId"))
        .willReturn(aResponse()
            .withStatus(200)));

    assertDoesNotThrow(() -> hotelServiceClient.reserveRoom(reservationRequest));

    verify(postRequestedFor(urlEqualTo("/api/reservations/reserve"))
        .withHeader("Content-Type", equalTo("application/json")));
  }

  @Test
  void confirmReservation_ShouldCallConfirmEndpoint() {
    stubFor(post(urlEqualTo("/api/reservations/confirm"))
        .withRequestBody(matchingJsonPath("$.bookingId"))
        .willReturn(aResponse()
            .withStatus(200)));

    assertDoesNotThrow(() -> hotelServiceClient.confirmReservation(reservationRequest));

    verify(postRequestedFor(urlEqualTo("/api/reservations/confirm"))
        .withHeader("Content-Type", equalTo("application/json")));
  }

  @Test
  void cancelReservation_ShouldCallCancelEndpoint() {
    Long roomId = 1L;
    Long bookingId = 100L;

    stubFor(post(urlEqualTo("/api/reservations/cancel?roomId=" + roomId + "&bookingId=" + bookingId))
        .willReturn(aResponse()
            .withStatus(200)));

    assertDoesNotThrow(() -> hotelServiceClient.cancelReservation(roomId, bookingId));

    verify(postRequestedFor(urlPathEqualTo("/api/reservations/cancel"))
        .withQueryParam("roomId", equalTo(roomId.toString()))
        .withQueryParam("bookingId", equalTo(bookingId.toString())));
  }

  @Test
  void incrementTimesBooked_ShouldCallIncrementEndpoint() {
    Long roomId = 1L;

    stubFor(post(urlEqualTo("/api/rooms/" + roomId + "/increment-bookings"))
        .willReturn(aResponse()
            .withStatus(200)));

    assertDoesNotThrow(() -> hotelServiceClient.incrementTimesBooked(roomId));

    verify(postRequestedFor(urlEqualTo("/api/rooms/" + roomId + "/increment-bookings")));
  }

  @Test
  void getRecommendedRooms_ShouldThrowException_WhenNoRoomsAvailable() {
    Long hotelId = 5L;
    LocalDate startDate = LocalDate.of(2024, 6, 1);
    LocalDate endDate = LocalDate.of(2024, 6, 5);

    stubFor(get(urlPathEqualTo("/api/rooms/recommend"))
        .withQueryParam("hotelId", equalTo(hotelId.toString()))
        .withQueryParam("startDate", equalTo("01.06.2024"))
        .withQueryParam("endDate", equalTo("05.06.2024"))
        .willReturn(aResponse()
            .withStatus(404)));

    assertThrows(feign.FeignException.NotFound.class, () -> {
        hotelServiceClient.getRecommendedRooms(hotelId, startDate, endDate);
    });
  }
}
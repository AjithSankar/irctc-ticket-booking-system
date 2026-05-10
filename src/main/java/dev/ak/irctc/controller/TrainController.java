package dev.ak.irctc.controller;

import dev.ak.irctc.dto.CheckoutSummaryDTO;
import dev.ak.irctc.dto.ClassAvailabilityDTO;
import dev.ak.irctc.dto.TrainSearchResponse;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.TrainSchedule;
import dev.ak.irctc.repository.PassengerRepository;
import dev.ak.irctc.repository.SeatInventoryRepository;
import dev.ak.irctc.repository.TrainRepository;
import dev.ak.irctc.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;
    private final SeatInventoryRepository seatInventoryRepository;
    private final PassengerRepository passengerRepository;

    public TrainController(TrainService trainService, SeatInventoryRepository seatInventoryRepository, PassengerRepository passengerRepository) {
        this.trainService = trainService;
        this.seatInventoryRepository = seatInventoryRepository;
        this.passengerRepository = passengerRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainSearchResponse>> searchTrains(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {

        List<TrainSearchResponse> trains = trainService.findTrains(from, to, date);

        return ResponseEntity.ok(trains);
    }

    @GetMapping("/{trainNo}/availability")
    public ResponseEntity<List<ClassAvailabilityDTO>> getSixDayAvailability(
            @PathVariable Integer trainNo,
            @RequestParam String classType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {

        List<ClassAvailabilityDTO> sixDayAvailability = new ArrayList<>();

        // Helper to map classType (SL) to coachPrefix (S) for the seat query
        String coachPrefix = switch (classType) {
            case "1A" -> "H";
            case "2A" -> "A";
            case "3A" -> "B";
            case "CC" -> "C";
            case "SL" -> "S";
            default -> "S";
        };

        for (int i = 0; i < 6; i++) {
            LocalDate checkDate = startDate.plusDays(i);
            long availableSeats = seatInventoryRepository.countAvailableSeats(trainNo, checkDate, coachPrefix);
            String availabilityStr;
            if (availableSeats > 0) {
                availabilityStr = "AVAILABLE-" + String.format("%04d", availableSeats);
            } else {
                // 2. If 0 seats, check the True Waiting List
                long wlCount = passengerRepository.countWaitingListPassengers(trainNo, checkDate, classType);
                availabilityStr = "WL-" + (wlCount + 1);
            }

            sixDayAvailability.add(new ClassAvailabilityDTO(checkDate, availabilityStr));
        }

        return ResponseEntity.ok(sixDayAvailability);
    }

    @GetMapping("/{trainNo}/checkout-info")
    public ResponseEntity<CheckoutSummaryDTO> getCheckoutSummary(@PathVariable Integer trainNo, @RequestParam String classType) {

        CheckoutSummaryDTO checkoutSummaryDTO = trainService.getCheckoutSummary(trainNo, classType);
        return ResponseEntity.ok(checkoutSummaryDTO);
    }
}
package dev.ak.irctc.service;

import dev.ak.irctc.dto.TrainResponseDTO;
import dev.ak.irctc.dto.TrainSearchResponse;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.TrainSchedule;
import dev.ak.irctc.repository.SeatInventoryRepository;
import dev.ak.irctc.repository.TrainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainService {

    private final TrainRepository trainRepository;
    private final SeatInventoryRepository seatInventoryRepository;

    public TrainService(TrainRepository trainRepository, SeatInventoryRepository seatInventoryRepository) {
        this.trainRepository = trainRepository;
        this.seatInventoryRepository = seatInventoryRepository;
    }

    public List<TrainSearchResponse> findTrains(String from, String to, String date) {
        // 1. Fetch valid trains from DB
        List<Train> trains = trainRepository.findTrainsBetweenStations(from, to);
        LocalDate parsedDate = LocalDate.parse(date);
        log.info("Trains found: {}", trains.size());

        // 2. Map to DTOs
        List<TrainSearchResponse> response = trains.stream().map(train -> {
            TrainSearchResponse dto = new TrainSearchResponse();
            dto.setId(String.valueOf(train.getTrainNo()));
            dto.setName(train.getTrainName());

            // Extract timings specific to the requested stations
            TrainSchedule sourceRoute = train.getRouteSchedules().stream()
                    .filter(r -> r.getStationCode().equals(from)).findFirst().get();
            TrainSchedule destRoute = train.getRouteSchedules().stream()
                    .filter(r -> r.getStationCode().equals(to)).findFirst().get();

            dto.setDepartureTime(sourceRoute.getDepartureTime().toString());
            dto.setArrivalTime(destRoute.getArrivalTime().toString());

            // Calculate duration
            Duration duration = Duration.between(sourceRoute.getDepartureTime(), destRoute.getArrivalTime());
            // Adjust for overnight trains (simplified)
            if(duration.isNegative()) duration = duration.plusHours(24);
            dto.setDuration(String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart()));

            Integer trainNo = train.getTrainNo();

            dto.setClasses(List.of(
                    buildClassDTO("SL", 350.0, trainNo, parsedDate, "S"),
                    buildClassDTO("3A", 950.0, trainNo, parsedDate, "B"),
                    buildClassDTO("2A", 1350.0, trainNo, parsedDate, "A")
            ));

            return dto;
        }).collect(Collectors.toList());

        return response;
    }

    private TrainSearchResponse.TrainClassDTO buildClassDTO(String classType, double price, Integer trainNo, LocalDate date, String coachPrefix) {
        long availableSeats = seatInventoryRepository.countAvailableSeats(trainNo, date, coachPrefix);

        String availabilityStr;
        if (availableSeats > 0) {
            // Format as "AVAILABLE-0024"
            availabilityStr = "AVAILABLE-" + String.format("%04d", availableSeats);
        } else {
            // In a full production app, you would count the WAITING_LIST table here.
            // For now, if 0 seats are available, we simulate a standard Waiting List.
            availabilityStr = "WL-15";
        }

        return new TrainSearchResponse.TrainClassDTO(classType, price, availabilityStr);
    }

    public List<TrainResponseDTO> getAllTrains() {
        List<Train> trains = trainRepository.findAll();

        return trains.stream()
                .map(train -> toTrainResponseDTO(train))
                .toList();
    }

    private TrainResponseDTO toTrainResponseDTO(Train train) {
        return new TrainResponseDTO(
                train.getId(),
                train.getTrainNo(),
                train.getTrainName(),
                train.getSourceStation(),
                train.getDestinationStation(),
                train.getRunsOn(),
                train.isActive()
        );
    }
}

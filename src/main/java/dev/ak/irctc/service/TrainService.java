package dev.ak.irctc.service;

import dev.ak.irctc.dto.TrainSearchResponse;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.TrainSchedule;
import dev.ak.irctc.repository.TrainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public List<TrainSearchResponse> findTrains(String from, String to, String date) {
        // 1. Fetch valid trains from DB
        List<Train> trains = trainRepository.findTrainsBetweenStations(from, to);

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

            // MOCK AVAILABILITY: In the next phase, we will build the concurrent
            // seating engine to fetch this live. For now, we return standard classes.
            dto.setClasses(List.of(
                    new TrainSearchResponse.TrainClassDTO("SL", 350.0, "AVAILABLE-50"),
                    new TrainSearchResponse.TrainClassDTO("3A", 950.0, "AVAILABLE-25")
            ));

            return dto;
        }).collect(Collectors.toList());

        return response;
    }
}

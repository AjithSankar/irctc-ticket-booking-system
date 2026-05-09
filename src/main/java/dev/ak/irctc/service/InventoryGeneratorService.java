package dev.ak.irctc.service;

import dev.ak.irctc.entity.SeatInventory;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.TrainComposition;
import dev.ak.irctc.enums.SeatStatus;
import dev.ak.irctc.repository.SeatInventoryRepository;
import dev.ak.irctc.repository.TrainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InventoryGeneratorService {

    private final TrainRepository trainRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    
    // IRCTC standard Advance Reservation Period
    private static final int ARP_DAYS = 60;

    public InventoryGeneratorService(TrainRepository trainRepository, SeatInventoryRepository seatInventoryRepository) {
        this.trainRepository = trainRepository;
        this.seatInventoryRepository = seatInventoryRepository;
    }

    /**
     * Runs every night at exactly Midnight (00:00:00).
     * Cron format: Second Minute Hour Day Month Weekday
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void generateDailyInventory() {
        LocalDate targetDate = LocalDate.now().plusDays(ARP_DAYS);
        log.info("Starting Midnight Batch Job: Generating Seat Inventory for target date: {}", targetDate);

        generateInventoryForDate(targetDate);
    }

    /**
     * Extracted logic so we can manually trigger it via an Admin API if needed!
     */
    public void generateInventoryForDate(LocalDate date) {
        List<Train> activeTrains = trainRepository.findAllByIsActiveTrue();

        for (Train train : activeTrains) {

            String dayOfWeek = date.getDayOfWeek().name().substring(0, 3); // e.g., "MON", "TUE"
            if (train.getRunsOn() != null && !train.getRunsOn().contains(dayOfWeek) && !train.getRunsOn().equalsIgnoreCase("DAILY")) {
                log.info("Skipping Train {} - Doesn't run on {}", train.getTrainNo(), dayOfWeek);
                continue;
            }

            // Idempotency check: Skip if seats already exist for this train on this date
            if (seatInventoryRepository.existsByTrainNoAndJourneyDate(train.getTrainNo(), date)) {
                log.warn("Inventory already exists for Train {} on {}. Skipping.", train.getTrainNo(), date);
                continue;
            }

            List<SeatInventory> newSeats = new ArrayList<>();

            for (TrainComposition comp : train.getComposition()) {
                for (int coachNum = 1; coachNum <= comp.getNumberOfCoaches(); coachNum++) {
                    String currentCoach = comp.getCoachPrefix() + coachNum; // e.g., "S1", "S2"

                    for (int seatNum = 1; seatNum <= comp.getSeatsPerCoach(); seatNum++) {
                        SeatInventory seat = new SeatInventory();
                        seat.setTrain(train);
                        seat.setJourneyDate(date);
                        seat.setSourceStation(train.getSourceStation());
                        seat.setDestinationStation(train.getDestinationStation());
                        seat.setCoach(currentCoach);
                        seat.setSeatNumber(seatNum);
                        seat.setStatus(SeatStatus.AVAILABLE);

                        newSeats.add(seat);
                    }
                }
            }

            // High-Performance Bulk Insert
            // (In production, if newSeats is > 10,000, you'd partition this list into chunks of 1000)
            seatInventoryRepository.saveAll(newSeats);
            log.info("Successfully generated {} seats for Train {} on {}", newSeats.size(), train.getTrainNo(), date);
        }
    }
}
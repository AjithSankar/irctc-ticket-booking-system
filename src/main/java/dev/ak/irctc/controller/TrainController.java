package dev.ak.irctc.controller;

import dev.ak.irctc.dto.TrainSearchResponse;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.TrainSchedule;
import dev.ak.irctc.repository.TrainRepository;
import dev.ak.irctc.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainSearchResponse>> searchTrains(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date) {

        List<TrainSearchResponse> trains = trainService.findTrains(from, to, date);

        return ResponseEntity.ok(trains);
    }
}
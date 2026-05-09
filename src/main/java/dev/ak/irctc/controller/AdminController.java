package dev.ak.irctc.controller;

import dev.ak.irctc.dto.TrainResponseDTO;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.repository.TrainRepository;
import dev.ak.irctc.service.InventoryGeneratorService;
import dev.ak.irctc.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TrainService trainService;
    private final InventoryGeneratorService inventoryGeneratorService;

    public AdminController(TrainService trainService, InventoryGeneratorService inventoryGeneratorService) {
        this.trainService = trainService;
        this.inventoryGeneratorService = inventoryGeneratorService;
    }

    @GetMapping("/trains")
    public ResponseEntity<List<TrainResponseDTO>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @PostMapping("/inventory/generate")
    public ResponseEntity<String> triggerInventoryGeneration(@RequestParam String targetDate) {
        LocalDate date = LocalDate.parse(targetDate);
        inventoryGeneratorService.generateInventoryForDate(date);
        return ResponseEntity.ok("Successfully generated seat inventory for " + date);
    }
}
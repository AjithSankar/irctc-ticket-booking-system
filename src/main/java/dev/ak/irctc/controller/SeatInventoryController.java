package dev.ak.irctc.controller;

import dev.ak.irctc.dto.SeatGenerateRequest;
import dev.ak.irctc.service.InventoryGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RequestMapping("/api/seats")
@RestController
@Slf4j
public class SeatInventoryController {

    private final InventoryGeneratorService inventoryGeneratorService;

    public SeatInventoryController(InventoryGeneratorService inventoryGeneratorService) {
        this.inventoryGeneratorService = inventoryGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateSeats(@RequestBody SeatGenerateRequest request) {
        String date = request.date();
        log.info("Received request to generate seats for date: {}", date);
        
        if (date == null || date.trim().isEmpty()) {
            log.warn("Invalid request - date parameter is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Date parameter cannot be null or empty");
        }

        LocalDate localDate;
        try {
            log.debug("Parsing date string: {}", date);
            localDate = LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format provided: {}. Expected format: YYYY-MM-DD", date, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format. Please use YYYY-MM-DD format");
        }

        if (localDate.isBefore(LocalDate.now())) {
            log.warn("Rejected request to generate seats for past date: {}", localDate);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot generate seats for a past date");
        }

        try {
            log.info("Starting seat inventory generation for date: {}", localDate);
            inventoryGeneratorService.generateInventoryForDate(localDate);
            log.info("Successfully completed seat inventory generation for date: {}", localDate);
            return ResponseEntity.ok("Seats generated successfully for date: " + localDate);
        } catch (Exception e) {
            log.error("Error occurred while generating seat inventory for date: {}", localDate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate seat inventory. Please try again later.");
        }
    }
}

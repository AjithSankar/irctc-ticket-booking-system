package dev.ak.irctc.service;

import dev.ak.irctc.dto.BookingRequestDTO;
import dev.ak.irctc.queue.BookingQueue;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookingWorker {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int QUEUE_CAPACITY = 5000;

    private final BookingQueue bookingQueue;
    private final SeatAllocationService seatAllocationService;
    private ExecutorService executorService;

    public BookingWorker(BookingQueue bookingQueue, SeatAllocationService seatAllocationService) {
        this.bookingQueue = bookingQueue;
        this.seatAllocationService = seatAllocationService;
    }

    @PostConstruct
    public void init() {
        try {
            executorService = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAX_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new java.util.concurrent.LinkedBlockingQueue<>(QUEUE_CAPACITY),
                    new ThreadPoolExecutor.AbortPolicy()
            );
            startWorkers();
            log.info("BookingWorker initialized with {} core threads", CORE_POOL_SIZE);
        } catch (Exception e) {
            log.error("Failed to initialize BookingWorker", e);
            throw new RuntimeException("Failed to initialize BookingWorker", e);
        }
    }

    private void startWorkers() {
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            try {
                executorService.submit(this::consume);
                log.info("Started worker thread {}", i + 1);
            } catch (Exception e) {
                log.error("Failed to start worker thread {}", i + 1, e);
            }
        }
    }

    public void consume() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BookingRequestDTO bookingRequestDTO = bookingQueue.take();
                log.info("Booking request received {}", bookingRequestDTO.bookingId());
                processBooking(bookingRequestDTO);
            } catch (InterruptedException e) {
                log.info("Worker thread interrupted, shutting down gracefully");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error processing booking request", e);
            }
        }
        log.info("Worker thread {} exiting", Thread.currentThread().getName());
    }

    private void processBooking(BookingRequestDTO bookingRequestDTO) {
        try {
            log.info("Processing booking request: {}", bookingRequestDTO.bookingId());
            seatAllocationService.allocateSeats(bookingRequestDTO);
            log.info("Completed booking request: {}", bookingRequestDTO.bookingId());
        } catch (Exception e) {
            log.error("Failed processing booking request: {}", bookingRequestDTO.bookingId(), e);
        }
    }

    /**
     * Get thread pool statistics for monitoring purposes
     */
    public String getThreadPoolStats() {
        if (executorService instanceof ThreadPoolExecutor tpe) {
            return String.format(
                "ThreadPool Stats - Active: %d, Queue Size: %d, Completed: %d, Total: %d",
                tpe.getActiveCount(),
                tpe.getQueue().size(),
                tpe.getCompletedTaskCount(),
                tpe.getTaskCount()
            );
        }
        return "ThreadPool stats unavailable";
    }

    @PreDestroy
    public void shutdownWorkers() {
        log.info("Initiating executor service shutdown");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Executor service did not terminate in time, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for executor service termination", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Executor service shutdown complete");
    }
}

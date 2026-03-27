package dev.ak.irctc.queue;

import dev.ak.irctc.dto.BookingRequestDTO;
import dev.ak.irctc.dto.QueuedBookingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class BookingQueue {

    private final BlockingQueue<BookingRequestDTO> queue = new LinkedBlockingQueue<>(10_000);

    public void publish(BookingRequestDTO request) {
        try {
            queue.put(request);
            log.info("Request {} added to queue. Queue size: {}", request.bookingId(), queue.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to enqueue booking request: {}", request, e);
        }
    }

    public BookingRequestDTO take() throws InterruptedException {
        return queue.take();
    }
}

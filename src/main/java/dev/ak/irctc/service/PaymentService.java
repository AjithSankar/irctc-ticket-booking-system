package dev.ak.irctc.service;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Transaction;
import dev.ak.irctc.enums.TransactionStatus;
import dev.ak.irctc.repository.BookingRepository;
import dev.ak.irctc.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public boolean processPayment(UUID bookingId, double amount, String idempotencyKey) {

        // Check existing transaction
        Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return existing.get().getStatus() == TransactionStatus.SUCCESS;
        }

        // Retrieve booking proxy reference (without loading full entity). This works only for Primary key.
        Booking bookingReference = bookingRepository.getReferenceById(bookingId);

        Transaction txn = Transaction.builder()
                .booking(bookingReference)
                .amount(amount)
                .idempotencyKey(idempotencyKey)
                .status(TransactionStatus.INITIATED)
                .build();

        transactionRepository.save(txn);

        // Simulate payment processing
        boolean success = simulatePaymentGateway();

        txn.setStatus(success ? TransactionStatus.SUCCESS : TransactionStatus.FAILED);
        transactionRepository.save(txn);

        log.info("Payment {} for booking {}", txn.getStatus().name(), bookingId);

        return success;
    }

    private boolean simulatePaymentGateway() {
        // 80% success rate
        return Math.random() < 0.99;
    }
}
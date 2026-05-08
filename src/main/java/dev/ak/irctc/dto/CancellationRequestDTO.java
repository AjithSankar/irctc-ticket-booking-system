package dev.ak.irctc.dto;

import java.util.List;
import java.util.UUID;

public record CancellationRequestDTO(
    List<UUID> passengerIdsToCancel
) {}
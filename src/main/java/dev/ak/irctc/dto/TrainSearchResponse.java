package dev.ak.irctc.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainSearchResponse {
    private String id;
    private String name;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private List<TrainClassDTO> classes;

    @Setter
    @Getter
    public static class TrainClassDTO {
        private String type;
        private double price;
        private String availability;
        
        public TrainClassDTO(String type, double price, String availability) {
            this.type = type;
            this.price = price;
            this.availability = availability;
        }
    }
}
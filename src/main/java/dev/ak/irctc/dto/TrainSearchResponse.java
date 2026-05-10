package dev.ak.irctc.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainSearchResponse {
    private String trainNo;
    private String trainName;
    private String sourceStation;
    private String destinationStation;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private List<TrainClassDTO> classes;

    @Setter
    @Getter
    public static class TrainClassDTO {
        private String classType;
        private double price;
        private String availability;
        
        public TrainClassDTO(String classType, double price, String availability) {
            this.classType = classType;
            this.price = price;
            this.availability = availability;
        }
    }
}
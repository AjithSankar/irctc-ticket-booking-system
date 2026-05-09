package dev.ak.irctc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class IrctcTicketBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(IrctcTicketBookingSystemApplication.class, args);
    }

}

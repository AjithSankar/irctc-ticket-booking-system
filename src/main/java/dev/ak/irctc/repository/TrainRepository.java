package dev.ak.irctc.repository;

import dev.ak.irctc.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    Optional<Train> findByTrainNo(Integer trainNumber);

    @Query("""
        SELECT t FROM Train t 
        JOIN t.routeSchedules r1 
        JOIN t.routeSchedules r2 
        WHERE r1.stationCode = :source 
          AND r2.stationCode = :destination 
          AND r1.stopSequence < r2.stopSequence
    """)
    List<Train> findTrainsBetweenStations(@Param("source") String source, @Param("destination") String destination);

    List<Train> findAllByIsActiveTrue();

}

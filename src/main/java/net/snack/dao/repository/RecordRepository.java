package net.snack.dao.repository;

import net.snack.dao.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query(value = "from Record t where purchaseDate BETWEEN :startDate AND :endDate")
    List<Record> getAllBetweenDates(@Param("startDate") Date startDate, @Param("endDate")Date endDate);
}

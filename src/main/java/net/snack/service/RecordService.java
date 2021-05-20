package net.snack.service;

import net.snack.dao.model.Record;
import net.snack.dao.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RecordService {

    @Autowired
    private RecordRepository repository;

    public List<Record> recordList() { return repository.findAll(); }

    public void save(Record record) { repository.save(record); }

    public Record get(long id) { return repository.findById(id).get(); }

    public void delete(long id) { repository.deleteById(id); }

    public List<Record> getAllBetweenDates(Date startDate, Date endDate) {
        return repository.getAllBetweenDates(startDate, endDate);
    }
}

package net.snack.service;

import net.snack.dao.model.Category;
import net.snack.dao.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> doctorList() { return repository.findAll(); }

    public void save(Category category) { repository.save(category); }

    public Category get(long id) { return repository.findById(id).get(); }

    public void delete(long id) { repository.deleteById(id); }

    public Category findByName(String name) {
        return repository.findByName(name);
    }

    public List<Category> showList() {
        return repository.findAllVisible(true);
    }

    public List<Category> findByAmount(Integer amount) {
        return repository.findByAmount(amount);
    }
}

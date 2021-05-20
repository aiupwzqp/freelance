package net.snack.dao.repository;

import net.snack.dao.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT * FROM CATEGORY WHERE NAME = ?1", nativeQuery = true)
    Category findByName(String name);

    @Query(value = "SELECT * FROM CATEGORY WHERE VISIBLE = ?1", nativeQuery = true)
    List<Category> findAllVisible(Boolean visible);

    @Query(value = "SELECT * FROM CATEGORY WHERE AMOUNT = ?1", nativeQuery = true)
    List<Category> findByAmount(Integer amount);

}

package net.snack.dao.model;

import net.snack.dao.model.Record;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    private Long id;
    private String name;
    private Double price;
    private Integer amount;
    private Boolean visible;

    private List<Record> records;

    public Category() {
    }

    public Category(String name, Double price, Integer amount, Boolean visible) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.visible = visible;
    }

    public Category(Long id, String name, Double price, Integer amount, Boolean visible, List<Record> records) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.visible = visible;
        this.records = records;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @OneToMany(targetEntity=Record.class, fetch = FetchType.EAGER, mappedBy = "category",
            cascade = CascadeType.ALL)
    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

}

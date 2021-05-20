package net.snack.dao.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Record {

    private Long id;
    private Date purchaseDate;

    private Category category;

    public Record() {
    }

    public Record(Long id, Date purchaseDate, Category category) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.category = category;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }


    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "category_id", nullable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

package com.payr.loan_service.model;


import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity // Marks this class as a JPA entity that will be mapped to a database table
@Table(name = "loan_types") // Specifies the table name in the database
public class LoanType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Auto-incremented ID (commonly used for MySQL, PostgreSQL, etc.)
    private Integer id;


    @Column(nullable = false, unique = true)
    private String name;


    @Column(nullable = false)
    private Double interestRate;


    @Column(nullable = false)
    private Integer tenureMonths;


    @Column(nullable = false)
    private BigDecimal minAmount;


    @Column(nullable = false)
    private BigDecimal maxAmount;


    @Column(nullable = false)
    private Boolean active = true;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}


package com.boroday.orm;

@Table(name = "Managers")
public class Manager extends Employee {
    @Column
    private double bonus;

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}

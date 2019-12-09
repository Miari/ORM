package com.boroday.orm.Entity;

@Table(name = "Managers")
public class Manager extends Employee {
    @Column
    private double bonus;

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}

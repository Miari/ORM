package com.boroday.orm;

abstract class Employee {
    @Column
    @Id
    private String inn;

    @Column
    private String name;

    @Column
    private double salary;

    public void setInn(String inn) {
        this.inn = inn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

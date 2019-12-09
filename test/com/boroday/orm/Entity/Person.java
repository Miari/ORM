package com.boroday.orm.Entity;

@Table(name = "Persons")
public class Person {
    @Column
    @Id
    private int id;

    @Column(name = "person_name")
    private String name;

    @Column
    private double salary;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

package com.boroday.querygenerator;

@Table (name = "Persons")
public class Person {
    @Column
    private int id;

    @Column (name = "person_name")
    @InsertColumn (name = "person_name")
    //@UpdateColumn (name = "person_name")
    private String name;

    @Column
    @InsertColumn
    @UpdateColumn
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }
}

package com.boroday.querygenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class QueryGeneratorTest {
    private QueryGenerator queryGenerator = new QueryGenerator();

    @Test
    public void testGetAll() {
        String getAllSql = queryGenerator.getAll(Person.class);
        String expectedSql = "SELECT id, person_name, salary FROM Persons;";
        assertEquals(expectedSql, getAllSql);
    }

    @Test
    public void testGetById() {
        int id = 1;
        String getByIdSql = queryGenerator.getByID(Person.class, id);
        String expectedSql = "SELECT * FROM Persons WHERE id = 1;";
        assertEquals(expectedSql, getByIdSql);
    }

    @Test
    public void testDelete() {
        int id = 1;
        String deleteByIdSql = queryGenerator.delete(Person.class, id);
        String expectedSqlDeleteById = "DELETE FROM Persons WHERE id = 1;";
        assertEquals(expectedSqlDeleteById, deleteByIdSql);
    }

    @Test
    public void testInsert() throws IllegalAccessException {
        Person person = new Person();
        person.setId(2);
        person.setName("Peter");
        person.setSalary(10.5);

        String insertSql = queryGenerator.insert(person);
        String expectedSqlInsert = "INSERT INTO Persons (id, person_name, salary) VALUES (2, 'Peter', 10.5);";
        assertEquals(expectedSqlInsert, insertSql);
    }

    @Test
    public void testUpdate() throws IllegalAccessException {
        Person person = new Person();
        person.setId(2);
        person.setName("Dina");
        person.setSalary(13.3);

        String updateSql = queryGenerator.update(person);
        String expectedSqlUpdate = "UPDATE Persons SET person_name = 'Dina', salary = 13.3 where id = 2;";
        assertEquals(expectedSqlUpdate, updateSql);
    }
}

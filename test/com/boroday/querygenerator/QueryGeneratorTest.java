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
    public void testInsert() {
        String insertSql = queryGenerator.insert("Peter");
        String expectedSqlInsert = "INSERT INTO Persons (person_name) VALUES ('Peter');";
        //String expectedSqlInsert = "INSERT INTO Persons (salary) VALUES (10.5);";
        assertEquals(expectedSqlInsert, insertSql);
    }

    @Test
    public void testUpdate() {
        String updateSql = queryGenerator.update(10.5);
        //String expectedSqlUpdate = "UPDATE Persons SET person_name = 'Dina' where id = 1;";
        String expectedSqlUpdate = "UPDATE Persons SET salary = 10.5 where id = 1;";
        assertEquals(expectedSqlUpdate, updateSql);
    }
}

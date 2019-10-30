package com.boroday.querygenerator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class QueryGeneratorTest {
    private QueryGenerator queryGenerator = new QueryGenerator();

    @Test
    public void testGetAll() {
        String getAllSql = queryGenerator.getAll(Person.class);

        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("id");
        expectedResult.add("salary");
        expectedResult.add("person_name");

        //firstPart
        String firstPart = "SELECT ";
        assertTrue(getAllSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (" FROM Persons;");
        int indexOfThirdPart = getAllSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(getAllSql.endsWith(thirdPart));

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = getAllSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        for (String fieldFromSql : fieldsFromSql) {
            assertTrue(expectedResult.remove(fieldFromSql));
        }
        assertTrue(expectedResult.isEmpty());
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

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("id", "2");
        expectedResult.put("salary", "10.5");
        expectedResult.put("person_name", "'Peter'");

        //firstPart
        String firstPart = "INSERT INTO Persons (";
        assertTrue(insertSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (") VALUES (");
        int indexOfThirdPart = insertSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = insertSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //fourthPart
        int lengthOfThirdPart = thirdPart.length();
        String fourthPart = insertSql.substring(indexOfThirdPart + lengthOfThirdPart, insertSql.length() - 2);

        //fifthPart
        assertTrue(insertSql.endsWith(");"));

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        String[] valuesFromSql = fourthPart.split(", ");
        for (int i = 0; i < fieldsFromSql.length; i++) {
            assertTrue(expectedResult.remove(fieldsFromSql[i], valuesFromSql[i]));
        }
        assertTrue(expectedResult.isEmpty());
    }

    @Test
    public void testUpdate() throws IllegalAccessException {
        Person person = new Person();
        person.setId(2);
        person.setName("Dina");
        person.setSalary(13.3);

        String updateSql = queryGenerator.update(person);

        HashMap<String, String> expectedResult = new HashMap<>();

        expectedResult.put("person_name", "'Dina'");

        //firstPart
        String firstPart = "UPDATE Persons SET ";
        assertTrue(updateSql.startsWith(firstPart));
        expectedResult.put("salary", "13.3");

        //thirdPart
        String thirdPart = (" where id = 2;");
        int indexOfThirdPart = updateSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(updateSql.endsWith(thirdPart));

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = updateSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsAndValuesFromSql = secondPart.split(", ");
        for (String fieldAndValueFromSql : fieldsAndValuesFromSql) {
            String[] fieldAndValueSeparate = fieldAndValueFromSql.split(" = ");
            assertTrue(expectedResult.remove(fieldAndValueSeparate[0], fieldAndValueSeparate[1]));
        }
        assertTrue(expectedResult.isEmpty());
    }
}

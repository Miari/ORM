package com.boroday.orm;

import com.boroday.orm.Entity.Manager;
import com.boroday.orm.Entity.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class QueryGeneratorTest {
    private QueryGenerator queryGenerator = new QueryGenerator();
    private String tableName;
    private String tableNameForSubclass;
    private ArrayList<String> expectedResultList;
    private ArrayList<String> expectedResultListManager;

    @Before //Before is used instead of BeforeClass as the full lists are required for two test cases
    public void dataPreparation() {
        tableName = "Persons";
        expectedResultList = new ArrayList<>();
        expectedResultList.add("id");
        expectedResultList.add("salary");
        expectedResultList.add("person_name");

        tableNameForSubclass = "Managers";
        expectedResultListManager = new ArrayList<>();
        expectedResultListManager.add("inn");
        expectedResultListManager.add("salary");
        expectedResultListManager.add("name");
        expectedResultListManager.add("bonus");
    }

    @Test(expected = NullPointerException.class)
    public void testGetAllIdWithNullClass() {
        queryGenerator.getAll(null);
    }

    @Test
    public void testGetAll() {
        String getAllSql = queryGenerator.getAll(Person.class);

        //firstPart
        String firstPart = "SELECT ";
        assertTrue(getAllSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (" FROM " + tableName + ";");
        int indexOfThirdPart = getAllSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(getAllSql.endsWith(thirdPart));

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = getAllSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        for (String fieldFromSql : fieldsFromSql) {
            assertTrue(expectedResultList.remove(fieldFromSql));
        }
        assertTrue(expectedResultList.isEmpty());
    }

    @Test
    public void testGetAllForSubclass() {
        String getAllSql = queryGenerator.getAll(Manager.class);
        //firstPart
        String firstPart = "SELECT ";
        assertTrue(getAllSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (" FROM " + tableNameForSubclass + ";");
        int indexOfThirdPart = getAllSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(getAllSql.endsWith(thirdPart));

        ///secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = getAllSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        for (String fieldFromSql : fieldsFromSql) {
            assertTrue(expectedResultListManager.remove(fieldFromSql));
        }
        assertTrue(expectedResultListManager.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testGetByIdWithNullObject() {
        queryGenerator.getById(Person.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetByIdWithNullClass() {
        queryGenerator.getById(null, 1);
    }

    @Test
    public void testGetById() {
        int id = 1;
        String getByIdSql = queryGenerator.getById(Person.class, id);

        //firstPart
        String firstPart = "SELECT ";
        assertTrue(getByIdSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (" FROM " + tableName + " WHERE id = 1;");
        int indexOfThirdPart = getByIdSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(getByIdSql.endsWith(thirdPart));

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = getByIdSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        for (String fieldFromSql : fieldsFromSql) {
            assertTrue(expectedResultList.remove(fieldFromSql));
        }
        assertTrue(expectedResultList.isEmpty());
    }

    @Test
    public void testGetByIdWithStringType() {
        String id = "Q5eF63DgFTH6";
        String getByIdSql = queryGenerator.getById(Manager.class, id);

        //firstPart
        String firstPart = "SELECT ";
        assertTrue(getByIdSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = (" FROM " + tableNameForSubclass + " WHERE inn = '" + id + "';");
        int indexOfThirdPart = getByIdSql.indexOf(thirdPart);
        assertTrue(indexOfThirdPart != -1);
        assertTrue(getByIdSql.endsWith(thirdPart));

        //secondPart
        int lengthOfFirstPart = firstPart.length();
        String secondPart = getByIdSql.substring(lengthOfFirstPart, indexOfThirdPart);

        //test fields and values
        String[] fieldsFromSql = secondPart.split(", ");
        for (String fieldFromSql : fieldsFromSql) {
            assertTrue(expectedResultListManager.remove(fieldFromSql));
        }
        assertTrue(expectedResultListManager.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWithNullObject() {
        queryGenerator.delete(Person.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteWithNullClass() {
        queryGenerator.delete(null, 1);
    }

    @Test
    public void testDelete() {
        int id = 1;
        String deleteByIdSql = queryGenerator.delete(Person.class, id);
        String expectedSqlDeleteById = "DELETE FROM " + tableName + " WHERE id = 1;";
        assertEquals(expectedSqlDeleteById, deleteByIdSql);
    }

    @Test
    public void testDeleteWithIdOfStringType() {
        String id = "Q5eF63DgFTH6";
        String deleteByIdSql = queryGenerator.delete(Manager.class, id);
        String expectedSqlDeleteById = "DELETE FROM " + tableNameForSubclass + " WHERE inn = '" + id + "';";
        assertEquals(expectedSqlDeleteById, deleteByIdSql);
    }

    @Test(expected = NullPointerException.class)
    public void testInsertWithNullObject() throws IllegalAccessException {
        queryGenerator.insert(null);
    }

    @Test
    public void testInsert() throws IllegalAccessException {
        //data preparation start
        HashMap<String, String> expectedResultMap = new HashMap<>();
        expectedResultMap.put("id", "2");
        expectedResultMap.put("salary", "10.5");
        expectedResultMap.put("person_name", "'Peter'");
        /*не могу убрать одинарные кавычки, так как insertSql, который я получаю ниже, это строка, из которой я
        анализирую отдельные части, который тоже строки. Соответственно, если я положу в expectedResultMap
        <String, Object> вместо <String, String> у меня перестаёт совпадать результат. А если я в HashMap всё ложу
        строками, то настоящую строку могу выделить только таким способом, с помощью одинарных кавычек
         */

        Person person = new Person();
        person.setId(2);
        person.setName("Peter");
        person.setSalary(10.5);
        //data preparation end

        String insertSql = queryGenerator.insert(person);
        //firstPart
        String firstPart = "INSERT INTO " + tableName + " (";
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
            assertTrue(expectedResultMap.remove(fieldsFromSql[i], valuesFromSql[i]));
        }
        assertTrue(expectedResultMap.isEmpty());
    }

    @Test
    public void testInsertForSubclass() throws IllegalAccessException {
        //data preparation start
        HashMap<String, String> expectedResultMap = new HashMap<>();
        expectedResultMap.put("inn", "'Q5eF63DgFTH6'");
        /*не могу убрать одинарные кавычки, так как insertSql, который я получаю ниже, это строка, из которой я
        анализирую отдельные части, который тоже строки. Соответственно, если я положу в expectedResultMap
        <String, Object> вместо <String, String> у меня перестаёт совпадать результат. А если я в HashMap всё ложу
        строками, то настоящую строку могу выделить только таким способом, с помощью одинарных кавычек
         */
        expectedResultMap.put("salary", "10.5");
        expectedResultMap.put("name", "null");
        expectedResultMap.put("bonus", "1.7");

        Manager manager = new Manager();
        manager.setInn("Q5eF63DgFTH6");
        manager.setName(null);
        manager.setSalary(10.5);
        manager.setBonus(1.7);
        //data preparation end

        String insertSql = queryGenerator.insert(manager);
        //firstPart
        String firstPart = "INSERT INTO " + tableNameForSubclass + " (";
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
        Object[] valuesFromSql = fourthPart.split(", ");
        for (int i = 0; i < fieldsFromSql.length; i++) {
            assertTrue(expectedResultMap.remove(fieldsFromSql[i], valuesFromSql[i]));
        }
        assertTrue(expectedResultMap.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateWithNullObject() throws IllegalAccessException {
        queryGenerator.update(null);
    }

    @Test
    public void testUpdate() throws IllegalAccessException {
        //data preparation start
        Person person = new Person();
        person.setId(2);
        person.setName("Dina");
        person.setSalary(13.3);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("person_name", "'Dina'");
        /*не могу убрать одинарные кавычки, так как insertSql, который я получаю ниже, это строка, из которой я
        анализирую отдельные части, который тоже строки. Соответственно, если я положу в expectedResultMap
        <String, Object> вместо <String, String> у меня перестаёт совпадать результат. А если я в HashMap всё ложу
        строками, то настоящую строку могу выделить только таким способом, с помощью одинарных кавычек*/

        expectedResult.put("salary", "13.3");
        //data preparation end

        String updateSql = queryGenerator.update(person);
        System.out.println(updateSql);
        //firstPart
        String firstPart = "UPDATE " + tableName + " SET ";
        assertTrue(updateSql.startsWith(firstPart));

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

    @Test
    public void testUpdateForSubClass() throws IllegalAccessException {
        String id = "Q5eF63DgFTH6";
        //data preparation start
        Manager manager = new Manager();
        manager.setInn("Q5eF63DgFTH6");
        manager.setName(null);
        manager.setSalary(13.3);
        manager.setBonus(1.7);

        HashMap<String, String> expectedResult = new HashMap<>();
        expectedResult.put("name", "null");
        expectedResult.put("salary", "13.3");
        expectedResult.put("bonus", "1.7");
        //data preparation end

        String updateSql = queryGenerator.update(manager);

        //firstPart
        String firstPart = "UPDATE " + tableNameForSubclass + " SET ";
        assertTrue(updateSql.startsWith(firstPart));

        //thirdPart
        String thirdPart = " where inn = " + id + ";";
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

    @Test (expected = NullPointerException.class)
    public void testGetTableAnnotationNull(){
        queryGenerator.getTableAnnotation(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetTableAnnotationWithoutAnnotation(){
        queryGenerator.getTableAnnotation(LinkedList.class);
    }
}

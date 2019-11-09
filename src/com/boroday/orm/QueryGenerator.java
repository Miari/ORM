package com.boroday.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class QueryGenerator {
    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Field> listOfAllFields = getAllParentFields(clazz);
        for (Field oneOfListOfAllFields : listOfAllFields) {
            Column columnAnnotation = oneOfListOfAllFields.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? oneOfListOfAllFields.getName() : columnAnnotationName;
                stringJoiner.add(columnName);
            }
        }
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String getById(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Field> listOfAllFields = getAllParentFields(clazz);
        String idColumnName = "";
        for (Field oneOfListOfAllFields : listOfAllFields) {
            Column columnAnnotation = oneOfListOfAllFields.getAnnotation(Column.class);
            Id idAnnotation = oneOfListOfAllFields.getAnnotation(Id.class);
            String oneOfListOfAllFieldsName = oneOfListOfAllFields.getName();
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? oneOfListOfAllFieldsName : columnAnnotationName;
                stringJoiner.add(columnName);
            }
            if (idAnnotation != null) {
                String idAnnotationName = idAnnotation.name();
                idColumnName = idAnnotationName.isEmpty() ? oneOfListOfAllFieldsName : idAnnotationName;
            }
        }
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(idColumnName);
        stringBuilder.append(" = ");
        if (id instanceof CharSequence) {
            stringBuilder.append("'");
            stringBuilder.append(id);
            stringBuilder.append("'");
        } else {
            stringBuilder.append(id);
        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        stringBuilder.append(tableName);

        List<Field> listOfAllFields = getAllParentFields(clazz);
        String idColumnName = "";
        for (Field oneOfListOfAllFields : listOfAllFields) {
            Id idAnnotation = oneOfListOfAllFields.getAnnotation(Id.class);
            if (idAnnotation != null) {
                String idAnnotationName = idAnnotation.name();
                idColumnName = idAnnotationName.isEmpty() ? oneOfListOfAllFields.getName() : idAnnotationName;
            }
        }

        stringBuilder.append(" WHERE ");
        stringBuilder.append(idColumnName);
        stringBuilder.append(" = ");
        if (id instanceof CharSequence) {
            stringBuilder.append("'");
            stringBuilder.append(id);
            stringBuilder.append("'");
        } else {
            stringBuilder.append(id);
        }
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String insert(Object value) throws IllegalAccessException {
        Class<?> clazz = value.getClass();

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        stringBuilder.append(tableName);
        stringBuilder.append(" (");

        StringJoiner stringJoinerFields = new StringJoiner(", ");
        StringJoiner stringJoinerValues = new StringJoiner(", ");

        List<Field> listOfAllFields = getAllParentFields(clazz);
        for (Field oneOfListOfAllFields : listOfAllFields) {
            Column columnAnnotation = oneOfListOfAllFields.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? oneOfListOfAllFields.getName() : columnAnnotationName;
                stringJoinerFields.add(columnName);
                oneOfListOfAllFields.setAccessible(true);
                Object valueOfDeclaredField = oneOfListOfAllFields.get(value);
                if (valueOfDeclaredField == null) {
                    stringJoinerValues.add("null");
                } else {
                    String valueOfDeclaredFieldAsString = valueOfDeclaredField.toString();
                    if (valueOfDeclaredField instanceof CharSequence) {
                        stringJoinerValues.add("'" + valueOfDeclaredFieldAsString + "'");
                    } else {
                        stringJoinerValues.add(valueOfDeclaredFieldAsString);
                    }
                }
                oneOfListOfAllFields.setAccessible(false);
            }
        }
        stringBuilder.append(stringJoinerFields);
        stringBuilder.append(") VALUES (");
        stringBuilder.append(stringJoinerValues);
        stringBuilder.append(");");
        return stringBuilder.toString();
    }


    public String update(Object value) throws IllegalAccessException {
        Class<?> clazz = value.getClass();

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        stringBuilder.append(annotation.name());
        stringBuilder.append(" SET ");
        StringJoiner stringJoiner = new StringJoiner(", ");

        List<Field> listOfAllFields = getAllParentFields(clazz);
        Object id = null;
        boolean isForUpdate = true;
        String idColumnName = "";

        for (Field oneOfListOfAllFields : listOfAllFields) {
            Column updateColumnAnnotation = oneOfListOfAllFields.getAnnotation(Column.class);
            Id idAnnotation = oneOfListOfAllFields.getAnnotation(Id.class);

            if (updateColumnAnnotation != null) {
                String updateColumnAnnotationName = updateColumnAnnotation.name();
                String columnName = updateColumnAnnotationName.isEmpty() ? oneOfListOfAllFields.getName() : updateColumnAnnotationName;
                oneOfListOfAllFields.setAccessible(true);
                Object valueOfDeclaredField = oneOfListOfAllFields.get(value);
                if (valueOfDeclaredField == null) {
                    stringJoiner.add(columnName + " = null");
                } else {
                    if (idAnnotation != null) {
                        String idAnnotationName = idAnnotation.name();
                        idColumnName = idAnnotationName.isEmpty() ? oneOfListOfAllFields.getName() : idAnnotationName;
                        id = valueOfDeclaredField;
                        isForUpdate = false;
                    }
                    if (isForUpdate) {
                        String valueOfDeclaredFieldAsString = valueOfDeclaredField.toString();
                        if (valueOfDeclaredField instanceof CharSequence) {
                            stringJoiner.add(columnName + " = '" + valueOfDeclaredFieldAsString + "'");
                        } else {
                            stringJoiner.add(columnName + " = " + valueOfDeclaredFieldAsString);
                        }
                    }
                    isForUpdate = true;
                }
                oneOfListOfAllFields.setAccessible(false);
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" where ");
        stringBuilder.append(idColumnName);
        stringBuilder.append(" = ");
        stringBuilder.append(id);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    private ArrayList<Field> getAllParentFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        ArrayList<Field> allFieldsOfClassIncludingParentFields = new ArrayList<>(Arrays.asList(declaredFields));
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            allFieldsOfClassIncludingParentFields.addAll(getAllParentFields(superClass));
        }
        return allFieldsOfClassIncludingParentFields;
    }
}

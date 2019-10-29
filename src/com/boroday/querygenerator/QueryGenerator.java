package com.boroday.querygenerator;

import java.lang.reflect.Field;
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
        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String getByID(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("SELECT * FROM ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(id);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE id = ");
        stringBuilder.append(id);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String insert(Object value) throws IllegalAccessException {
        Class<?> clazz = value.getClass();

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO Persons (");
        StringJoiner stringJoinerFields = new StringJoiner(", ");
        StringJoiner stringJoinerValues = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column ColumnAnnotation = declaredField.getAnnotation(Column.class);
            if (ColumnAnnotation != null) {
                String columnName = ColumnAnnotation.name().isEmpty() ? declaredField.getName() : ColumnAnnotation.name();
                stringJoinerFields.add(columnName);
                declaredField.setAccessible(true);
                if (declaredField.getType().getName().equals("java.lang.String")) {
                    stringJoinerValues.add("'" + declaredField.get(value).toString() + "'");
                } else {
                    stringJoinerValues.add(declaredField.get(value).toString());
                }
                declaredField.setAccessible(false);
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
        int id = 0;

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column updateColumnAnnotation = declaredField.getAnnotation(Column.class);
            if (updateColumnAnnotation != null) {
                String columnName = updateColumnAnnotation.name().isEmpty() ? declaredField.getName() : updateColumnAnnotation.name();
                declaredField.setAccessible(true);
                if (declaredField.getName().equals("id")) {
                    id = declaredField.getInt(value);
                } else {
                    if (declaredField.getType().getName().equals("java.lang.String")) {
                        stringJoiner.add(columnName + " = '" + declaredField.get(value).toString() + "'");
                    } else {
                        stringJoiner.add(columnName + " = " + declaredField.get(value).toString());
                    }
                }
                declaredField.setAccessible(false);
            }
        }
        stringBuilder.append(stringJoiner);
        stringBuilder.append(" where id = ");
        stringBuilder.append(id);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }
}

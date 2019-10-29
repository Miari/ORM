package com.boroday.querygenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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

    public String insert(Object value) {
        Class<?> clazz = Person.class;

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO Persons (");

        for (Field declaredField : clazz.getDeclaredFields()) {
            InsertColumn insertColumnAnnotation = declaredField.getAnnotation(InsertColumn.class);
            if (insertColumnAnnotation != null) {
                String columnName = insertColumnAnnotation.name().isEmpty() ? declaredField.getName() : insertColumnAnnotation.name();
                stringBuilder.append(columnName);

                Type typeOfDeclaredField = declaredField.getType();

                if (typeOfDeclaredField.getTypeName().equals("java.lang.String")) {
                    stringBuilder.append(") VALUES ('");
                    stringBuilder.append(value);
                    stringBuilder.append("');");
                } else {
                    stringBuilder.append(") VALUES (");
                    stringBuilder.append(value);
                    stringBuilder.append(");");
                }
                break;
            }
        }
        return stringBuilder.toString();
    }

    public String update(Object value) {
        Class<?> clazz = Person.class;

        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        stringBuilder.append(annotation.name());
        stringBuilder.append(" SET ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            UpdateColumn updateColumnAnnotation = declaredField.getAnnotation(UpdateColumn.class);
            if (updateColumnAnnotation != null) {
                String columnName = updateColumnAnnotation.name().isEmpty() ? declaredField.getName() : updateColumnAnnotation.name();

                Type typeOfDeclaredField = declaredField.getType();
                stringBuilder.append(columnName);
                if (typeOfDeclaredField.getTypeName().equals("java.lang.String")) {
                    stringBuilder.append(" = '");
                    stringBuilder.append(value);
                    stringBuilder.append("' where id = 1;");
                } else {
                    stringBuilder.append(" = ");
                    stringBuilder.append(value);
                    stringBuilder.append(" where id = 1;");
                }
                break;
            }
        }
        return stringBuilder.toString();
    }
}

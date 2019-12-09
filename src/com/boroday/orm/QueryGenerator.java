package com.boroday.orm;

import com.boroday.orm.Entity.Column;
import com.boroday.orm.Entity.Id;
import com.boroday.orm.Entity.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class QueryGenerator {
    public String getAll(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Field> listOfAllFields = getAllParentFields(clazz);
        for (Field field : listOfAllFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? field.getName() : columnAnnotationName;
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
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        StringJoiner stringJoiner = new StringJoiner(", ");
        List<Field> listOfAllFields = getAllParentFields(clazz);
        String idColumnName = "";

        for (Field field : listOfAllFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            Id idAnnotation = field.getAnnotation(Id.class);
            String oneOfListOfAllFieldsName = field.getName();
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? oneOfListOfAllFieldsName : columnAnnotationName;
                stringJoiner.add(columnName);
                if (idAnnotation != null) {
                    idColumnName = columnName;
                }
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");

        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        stringBuilder.append(tableName);
        stringBuilder.append(" WHERE ");
        stringBuilder.append(idColumnName);
        stringBuilder.append(" = ");
        wrapIfNeededAndAppend(id, stringBuilder);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        stringBuilder.append(tableName);

        List<Field> listOfAllFields = getAllParentFields(clazz);
        String idColumnName = "";
        for (Field field : listOfAllFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            Id idAnnotation = field.getAnnotation(Id.class);
            String oneOfListOfAllFieldsName = field.getName();
            if (columnAnnotation != null) {
                String columnAnnotationName = columnAnnotation.name();
                String columnName = columnAnnotationName.isEmpty() ? oneOfListOfAllFieldsName : columnAnnotationName;
                if (idAnnotation != null) {
                    idColumnName = columnName;
                }
            }
        }

        stringBuilder.append(" WHERE ");
        stringBuilder.append(idColumnName);
        stringBuilder.append(" = ");
        wrapIfNeededAndAppend(id, stringBuilder);
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
        StringBuilder stringBuilderValues = new StringBuilder();

        List<Field> listOfAllFields = getAllParentFields(clazz);
        for (Field field : listOfAllFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                continue;
            }
            String columnAnnotationName = columnAnnotation.name();
            String columnName = columnAnnotationName.isEmpty() ? field.getName() : columnAnnotationName;
            stringJoinerFields.add(columnName);
            field.setAccessible(true);
            Object valueOfDeclaredField = field.get(value);
            if (valueOfDeclaredField == null) {
                stringBuilderValues.append("null");
            } else {
                wrapIfNeededAndAppend(valueOfDeclaredField, stringBuilderValues);
            }
            stringJoinerValues.add(stringBuilderValues);
            stringBuilderValues.delete(0, stringBuilderValues.length());
            field.setAccessible(false);
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
        StringBuilder stringBuilderValues = new StringBuilder();

        List<Field> listOfAllFields = getAllParentFields(clazz);
        Object id = null;
        String idColumnName = "";

        for (Field field : listOfAllFields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            Id idAnnotation = field.getAnnotation(Id.class);

            if (columnAnnotation == null) {
                continue;
            }
            String columnAnnotationName = columnAnnotation.name();
            String columnName = columnAnnotationName.isEmpty() ? field.getName() : columnAnnotationName;
            field.setAccessible(true);
            Object valueOfDeclaredField = field.get(value);
            if (valueOfDeclaredField == null) {
                stringJoiner.add(columnName + " = null");
            } else {
                if (idAnnotation != null) {
                    idColumnName = columnName;
                    id = valueOfDeclaredField;
                } else {
                    stringBuilderValues.append(columnName);
                    stringBuilderValues.append(" = ");
                    wrapIfNeededAndAppend(valueOfDeclaredField, stringBuilderValues);
                    stringJoiner.add(stringBuilderValues);
                    stringBuilderValues.delete(0, stringBuilderValues.length());
                }
            }
            field.setAccessible(false);
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

    private void wrapIfNeededAndAppend(Object value, StringBuilder stringBuilder) {
        if (value instanceof CharSequence) {
            stringBuilder.append("'");
            stringBuilder.append(value);
            stringBuilder.append("'");
        } else {
            stringBuilder.append(value);
        }
    }
}

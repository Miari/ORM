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
        classIsNull(clazz);
        StringBuilder stringBuilder = getSelectPartOfQuery(clazz);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String getById(Class<?> clazz, Object id) {
        classIsNull(clazz);
        objectValueIsNull(id);
        StringBuilder stringBuilder = getSelectPartOfQuery(clazz);
        getWherePartOfQuery(clazz, id, stringBuilder);
        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        classIsNull(clazz);
        objectValueIsNull(id);
        Table annotation = getTableAnnotation(clazz);

        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        stringBuilder.append(tableName);

        getWherePartOfQuery(clazz, id, stringBuilder);
        return stringBuilder.toString();
    }

    public String insert(Object value) throws IllegalAccessException {
        objectValueIsNull(value);
        Class<?> clazz = value.getClass();

        Table annotation = getTableAnnotation(clazz);
        String annotationName = annotation.name();
        String tableName = annotationName.isEmpty() ? clazz.getName() : annotationName;
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        stringBuilder.append(tableName);

        StringJoiner stringJoinerFields = new StringJoiner(", ", " (", ") VALUES ");
        StringJoiner stringJoinerValues = new StringJoiner(", ", "(", ");");

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
                stringJoinerValues.add("null");
            } else {
                stringJoinerValues.add(wrapIfNeededAndAppend(valueOfDeclaredField));
            }
            field.setAccessible(false);
        }
        stringBuilder.append(stringJoinerFields);
        stringBuilder.append(stringJoinerValues);
        return stringBuilder.toString();
    }


    public String update(Object value) throws IllegalAccessException {
        objectValueIsNull(value);
        Class<?> clazz = value.getClass();

        Table annotation = getTableAnnotation(clazz);

        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        stringBuilder.append(annotation.name());
        stringBuilder.append(" SET ");
        StringJoiner stringJoiner = new StringJoiner(", ");

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
                stringJoiner.add(createEqualString(columnName, null));
            } else {
                if (idAnnotation != null) {
                    idColumnName = columnName;
                    id = valueOfDeclaredField;
                } else {
                    stringJoiner.add(createEqualString(columnName, wrapIfNeededAndAppend(valueOfDeclaredField)));
                }
            }
            field.setAccessible(false);
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" where ");
        stringBuilder.append(createEqualString(idColumnName, id));
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    private String createEqualString(String columnName, Object id) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(columnName);
        stringBuilder.append(" = ");
        stringBuilder.append(id);
        return stringBuilder.toString();
    }


    private StringBuilder getSelectPartOfQuery(Class<?> clazz) {
        Table annotation = getTableAnnotation(clazz);
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
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
        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();
        stringBuilder.append(tableName);
        return stringBuilder;
    }

    private void getWherePartOfQuery(Class<?> clazz, Object id, StringBuilder stringBuilder) {
        String idColumnName = "";
        List<Field> listOfAllFields = getAllParentFields(clazz);
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
        stringBuilder.append(createEqualString(idColumnName, wrapIfNeededAndAppend(id)));
        stringBuilder.append(";");
    }

    Table getTableAnnotation(Class<?> clazz) {
        classIsNull(clazz);
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        return annotation;
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

    private String wrapIfNeededAndAppend(Object value) {
        StringBuilder stringBuilder = new StringBuilder();
        if (value instanceof CharSequence) {
            stringBuilder.append("'");
            stringBuilder.append(value);
            stringBuilder.append("'");
        } else {
            stringBuilder.append(value);
        }
        return stringBuilder.toString();
    }

    private void classIsNull(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Class is null");
        }
    }

    private void objectValueIsNull(Object value) {
        if (value == null) {
            throw new NullPointerException("Object value is null");
        }
    }
}

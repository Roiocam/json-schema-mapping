/* (C)2025 */
package io.github.roiocam.jsm.reflect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassFields {
    private Map<String, Field> fieldMap = new HashMap<>();
    private Map<String, TypeInfo> fieldTypeMap = new HashMap<>();

    public Set<String> getFieldNames() {
        HashSet<String> res = new HashSet<>(fieldMap.keySet());
        res.addAll(fieldTypeMap.keySet());
        return res;
    }

    public void putField(String fieldName, Field field, Class<?> fieldType) {
        this.putField(fieldName, field, fieldType, null);
    }

    public void putField(
            String fieldName, Field field, Class<?> fieldType, Class<?> parameterizedType) {
        fieldMap.put(fieldName, field);
        if (parameterizedType != null) {
            fieldTypeMap.put(fieldName, new TypeInfo(fieldType, parameterizedType));
        } else {
            fieldTypeMap.put(fieldName, new TypeInfo(fieldType));
        }
    }

    public boolean containsField(String fieldName) {
        return fieldMap.containsKey(fieldName) && fieldTypeMap.containsKey(fieldName);
    }

    public Field getField(String fieldName) {
        return fieldMap.get(fieldName);
    }

    public Class<?> getFieldType(String fieldName) {
        TypeInfo typeInfo = fieldTypeMap.get(fieldName);
        return typeInfo.getClazz();
    }

    public Class<?> getParameterizedType(String fieldName) {
        TypeInfo typeInfo = fieldTypeMap.get(fieldName);
        return typeInfo.getParameterizedType();
    }
}

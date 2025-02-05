/* (C)2025 */
package io.github.roiocam.jsm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {

    /**
     * Get all fields from the class and its super class
     *
     * @param clazz
     * @return
     */
    public static ClassFields getClassFields(Class<?> clazz) {
        if (clazz.equals(Class.class)) {
            return null;
        }
        ClassFields classFields = new ClassFields();
        Map<String, Map<TypeVariable, Class<?>>> parameterizedTypeMap = new HashMap<>();

        while (clazz != null) {
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<? extends Class<?>>[] typeParameters =
                        clazz.getSuperclass().getTypeParameters();
                if (typeParameters.length > 0
                        && actualTypeArguments.length > 0
                        && typeParameters.length == actualTypeArguments.length) {
                    Map<TypeVariable, Class<?>> typeMap = new HashMap<>();
                    for (int i = 0; i < typeParameters.length; i++) {
                        typeMap.put(typeParameters[i], (Class<?>) actualTypeArguments[i]);
                    }
                    parameterizedTypeMap.put(clazz.getSuperclass().getName(), typeMap);
                }
            }

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                Type fieldType = field.getGenericType();
                if (fieldType instanceof TypeVariable<?>
                        && parameterizedTypeMap.containsKey(clazz.getName())) {
                    TypeVariable<?> typeVariable = (TypeVariable<?>) fieldType;
                    Map<TypeVariable, Class<?>> typeMap = parameterizedTypeMap.get(clazz.getName());
                    if (typeMap.containsKey(typeVariable)) {
                        fieldType = typeMap.get(typeVariable);
                    }
                }
                if (fieldType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) fieldType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Type actualTypeArgument = actualTypeArguments[0];
                    if (actualTypeArgument instanceof TypeVariable<?>) {
                        TypeVariable<?> typeVariable = (TypeVariable<?>) actualTypeArgument;
                        if (parameterizedTypeMap.containsKey(clazz.getName())) {
                            Map<TypeVariable, Class<?>> typeMap =
                                    parameterizedTypeMap.get(clazz.getName());
                            if (typeMap.containsKey(typeVariable)) {
                                actualTypeArgument = typeMap.get(typeVariable);
                            }
                        }
                    }
                    classFields.putField(
                            fieldName, field, field.getType(), (Class<?>) actualTypeArgument);
                } else if (fieldType instanceof Class<?>) {
                    classFields.putField(fieldName, field, (Class<?>) fieldType);
                } else if (fieldType instanceof GenericArrayType) {
                    classFields.putField(
                            fieldName,
                            field,
                            ((GenericArrayType) fieldType).getGenericComponentType().getClass());
                } else {
                    throw new UnsupportedOperationException("Unsupported field type: " + fieldType);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return classFields;
    }
}

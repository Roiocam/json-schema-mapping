/* (C)2025 */
package io.github.roiocam.jsm.schema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public enum SchemaTypeMetadata {
    STRING("string", String.class, null, false),
    INT("int", Integer.class, int.class, false),
    BOOLEAN("boolean", Boolean.class, boolean.class, false),
    DOUBLE("double", Double.class, double.class, false),
    FLOAT("float", Float.class, float.class, false),
    LONG("long", Long.class, long.class, false),
    CHAR("char", Character.class, char.class, false),
    BYTE("byte", Byte.class, byte.class, false),
    SHORT("short", Short.class, short.class, false),
    LIST("list", List.class, null, true),
    SET("set", Set.class, null, true),
    ARRAY("array", null, null, true),
    BIG_INTEGER("big_integer", java.math.BigInteger.class, null, false),
    BIG_DECIMAL("big_decimal", java.math.BigDecimal.class, null, false),
    ;

    private final String type;
    private final Class<?> clazz;
    private final Class<?> primitiveClass;
    private final boolean isCollection;

    SchemaTypeMetadata(String type, Class<?> clazz, Class<?> primitiveClass, boolean isCollection) {
        this.type = type;
        this.clazz = clazz;
        this.primitiveClass = primitiveClass;
        this.isCollection = isCollection;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getType() {
        return type;
    }

    public Class<?> getPrimitiveClass() {
        return primitiveClass;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public static SchemaTypeMetadata fromString(String text) {
        for (SchemaTypeMetadata b : SchemaTypeMetadata.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new UnsupportedOperationException("Unknown type: " + text);
    }

    public static SchemaTypeMetadata fromClass(Class<?> clazz) {
        if (clazz.isArray()) {
            return ARRAY;
        }
        for (SchemaTypeMetadata b : SchemaTypeMetadata.values()) {
            if (b == ARRAY) {
                continue;
            }
            if (b.clazz.equals(clazz)
                    || (b.primitiveClass != null && b.primitiveClass.equals(clazz))) {
                return b;
            }
            Type[] interfaces = clazz.getGenericInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                for (Type type : interfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        if (parameterizedType.getRawType().equals(b.clazz)) {
                            return b;
                        }
                    }
                }
            }
        }
        return null;
    }
}

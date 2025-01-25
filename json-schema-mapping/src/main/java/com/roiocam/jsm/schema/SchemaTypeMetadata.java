/* (C)2025 */
package com.roiocam.jsm.schema;

public enum SchemaTypeMetadata {
    STRING("string", String.class, null),
    INT("int", Integer.class, int.class),
    BOOLEAN("boolean", Boolean.class, boolean.class),
    DOUBLE("double", Double.class, double.class),
    FLOAT("float", Float.class, float.class),
    LONG("long", Long.class, long.class);

    private final String type;
    private final Class<?> clazz;
    private final Class<?> primitiveClass;

    SchemaTypeMetadata(String type, Class<?> clazz, Class<?> primitiveClass) {
        this.type = type;
        this.clazz = clazz;
        this.primitiveClass = primitiveClass;
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

    public static SchemaTypeMetadata fromString(String text) {
        for (SchemaTypeMetadata b : SchemaTypeMetadata.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new UnsupportedOperationException("Unknown type: " + text);
    }

    public static SchemaTypeMetadata fromClass(Class<?> clazz) {
        for (SchemaTypeMetadata b : SchemaTypeMetadata.values()) {
            if (b.clazz.equals(clazz)
                    || (b.primitiveClass != null && b.primitiveClass.equals(clazz))) {
                return b;
            }
        }
        throw new UnsupportedOperationException("Unknown type class: " + clazz);
    }
}

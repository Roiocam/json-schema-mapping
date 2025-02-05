/* (C)2025 */
package io.github.roiocam.jsm.reflect;

public class TypeInfo {
    private Class<?> clazz;
    private boolean isParameterized;
    private Class<?> parameterizedType;

    public TypeInfo(Class<?> clazz, Class<?> parameterizedType) {
        this.clazz = clazz;
        this.parameterizedType = parameterizedType;
        this.isParameterized = true;
    }

    public TypeInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.isParameterized = false;
        this.parameterizedType = null;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Class<?> getParameterizedType() {
        return parameterizedType;
    }

    public boolean isParameterized() {
        return isParameterized;
    }
}

package de.cjdev.papermodapi;

import java.lang.reflect.Field;

public class UnsafeFieldReplacer {
    private static final Object unsafe;
    private static final Class<?> unsafeClass;

    static {
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access sun.misc.Unsafe", e);
        }
    }

    public static void setFinal(Object targetObject, String fieldName, Object newValue) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Schrödinger, was hast du uns angetan :sob:
            var schroedinger = field.get(targetObject);

            long offset = (long) invokeUnsafe("objectFieldOffset", field);

            if (field.getType().isPrimitive()) {
                throw new IllegalArgumentException("Primitive types not supported — use an object reference.");
            }

            invokeUnsafe("getAndSetObject", targetObject, offset, newValue);
            //invokeUnsafe("putObject", staticBase, offset, newValue);

        } catch (Throwable t) {
            throw new RuntimeException("Failed to replace final field", t);
        }
    }

    public static void setStaticFinal(Class<?> targetClass, String fieldName, Object newValue) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);

            // Schrödinger, was hast du uns angetan :sob:
            var schroedinger = field.get(null);

            Object staticBase = invokeUnsafe("staticFieldBase", field);
            long offset = (long) invokeUnsafe("staticFieldOffset", field);

            if (field.getType().isPrimitive()) {
                throw new IllegalArgumentException("Primitive types not supported — use an object reference.");
            }

            invokeUnsafe("putObject", staticBase, offset, newValue);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to replace static final field", t);
        }
    }

    private static Object invokeUnsafe(String method, Object... args) throws Throwable {
        for (var m : unsafeClass.getDeclaredMethods()) {
            if (m.getName().equals(method) && m.getParameterCount() == args.length) {
                m.setAccessible(true);
                return m.invoke(unsafe, args); // hm
            }
        }
        throw new NoSuchMethodException("Method " + method + " not found in Unsafe");
    }

}
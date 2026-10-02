package dev.tkspring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Utils {

    public static void findMethodsByAnnotation(
            String[] packages,
            Class<? extends Annotation> classAnnotation,
            Class<? extends Annotation> methodAnnotation,
            Consumer<Method> dest
    ) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        for (String pkg : packages) {
            findClasses(pkg, classes);
        }

        for (Class<?> clazz : classes) {
            if (classAnnotation == null || clazz.isAnnotationPresent(classAnnotation)) {
                for (Method method : clazz.getMethods()) {
                    if (methodAnnotation == null || method.isAnnotationPresent(methodAnnotation)) {
                        dest.accept(method);
                    }
                }
            }
        }
    }

    private static void findClasses(String pkg, Set<Class<?>> dest) throws Exception {
        String pkgPath = pkg.replace('.', '/');
        File pkgDir = new File(Thread.currentThread().getContextClassLoader().getResource(pkgPath).toURI());

        for (File file : pkgDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = pkg + "." + file.getName().replace(".class", "");
                dest.add(Class.forName(className));
            }
            else if (file.isDirectory()) {
                findClasses(pkg + "." + file.getName(), dest);
            }
        }
    }

    public static Object parse(String str, Class<?> type) {
        try {
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                if (str == null || str.equalsIgnoreCase("false") || str.equals("0")) {
                    return false;
                }
                return true;
            }
            else if (type.equals(char.class) || type.equals(Character.class)) {
                if (str == null || str.length() == 0) {
                    return type.equals(char.class) ? '\0' : null;
                }
                return str.charAt(0);
            }
            else if (type.isPrimitive()) {
                if (str == null) {
                    return 0;
                }
                if (type == byte.class)       return Byte.parseByte(str);
                if (type == short.class)      return Short.parseShort(str);
                if (type == int.class)        return Integer.parseInt(str);
                if (type == long.class)       return Long.parseLong(str);
                if (type == float.class)      return Float.parseFloat(str);
                if (type == double.class)     return Double.parseDouble(str);
            }
            else if (Number.class.isAssignableFrom(type)) {
                if (str == null) {
                    return null;
                }
                if (type == Byte.class)       return Byte.valueOf(str);
                if (type == Short.class)      return Short.valueOf(str);
                if (type == Integer.class)    return Integer.valueOf(str);
                if (type == Long.class)       return Long.valueOf(str);
                if (type == Float.class)      return Float.valueOf(str);
                if (type == Double.class)     return Double.valueOf(str);
                if (type == BigInteger.class) return new BigInteger(str);
                if (type == BigDecimal.class) return new BigDecimal(str);
            }
            else if (type.equals(String.class)) {
                return str;
            }
            else if (type == Date.class) {
                return Date.from(Instant.parse(str));
            }
            if (type == LocalDate.class) {
                return LocalDate.parse(str);
            }
            if (type == LocalDateTime.class) {
                return LocalDateTime.parse(str);
            }

            return null;
        }
        catch (Exception e) {
            return type.isPrimitive() ? 0 : null;
        }
    }
}

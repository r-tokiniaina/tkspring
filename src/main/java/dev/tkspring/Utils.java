package dev.tkspring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean isSimpleType(Class<?> type) {
        return Set.of(
            boolean.class,      Boolean.class,
            char.class,         Character.class,
            byte.class,         Byte.class,
            short.class,        Short.class,
            int.class,          Integer.class,
            long.class,         Long.class,
            float.class,        Float.class,
            double.class,       Double.class,
            BigInteger.class,   BigDecimal.class,
            String.class,       Date.class,
            LocalDate.class,    LocalDateTime.class
        ).contains(type);
    }

    public static Object parse(Object object, Class<?> type) {
        if (type.isArray()) {
            return parseArray(object, type);
        }
        else {
            if (object != null && object.getClass().isArray()) {
                if (Array.getLength(object) == 0) {
                    object = null;
                }
                else {
                    object = Array.get(object, 0);
                }
            }
            if (isSimpleType(type)) {
                return parseString(object.toString(), type);
            }
            else {
                return parseObject((Map<String, ?>) object, type);
            }
        }
    }

    public static Object parseArray(Object array, Class<?> type) {
        Class<?> elementType = type.getComponentType();
        Object objects;
        if (array == null) {
            objects = Array.newInstance(elementType, 0);
        }
        else if (! array.getClass().isArray()) {
            objects = Array.newInstance(elementType, 1);
            Array.set(objects, 0, parse(array, elementType));
        }
        else {
            int length = Array.getLength(array);
            objects = Array.newInstance(elementType, length);
            for (int i = 0; i < length; i++) {
                Array.set(objects, i, parse(Array.get(array, i), elementType));
            }
        }
        return objects;
    }

    public static Object parseString(String str, Class<?> type) {
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

    public static Object parseObject(Map<String, ?> map, Class<?> type) {
        try {
            Object object = type.getConstructor().newInstance();

            if (map == null) {
                return object;
            }

            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String fieldName = entry.getKey();
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                try {
                    Class<?> fieldType = type.getDeclaredField(fieldName).getType();
                    Method setter = type.getMethod(setterName, fieldType);
                    Object value = parse(entry.getValue(), fieldType);
                    if (value != null) {
                        setter.invoke(object, value);
                    }
                }
                catch (Exception e) {
                    // Do nothing
                }
            }
            return object;
        }
        catch (Exception e) {
            return null;
        }
    }

    // Returns Map<String, Object> where Object = String | String[] | Map<String, Object>
    // It transforms: {             to: {
    //                  A: 1,             A: 1,
    //                  B-C: 2,           B: {
    //                  B-D: 3              C: 2,
    //                }                     D: 3
    //                                    }
    //                                  }
    public static Map<String, Object> getRequestParameterTree(HttpServletRequest request) {
        Map<String, Object> tree = new HashMap<>();

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] parts = entry.getKey().split("-");
            String[] values = entry.getValue();

            if (values == null || values.length == 0) {
                continue;
            }

            Map<String, Object> subtree = tree;
            for (int i = 0; i < parts.length - 1; i++) {
                subtree.putIfAbsent(parts[i], new HashMap<String, Object>());
                subtree = (Map<String, Object>) subtree.get(parts[i]);
            }
            subtree.put(
                parts[parts.length - 1],
                values.length == 1 ? values[0] : values
            );
        }

        return tree;
    }
}

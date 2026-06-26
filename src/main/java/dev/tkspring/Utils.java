package dev.tkspring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.io.File;

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
}

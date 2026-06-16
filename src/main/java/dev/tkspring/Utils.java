package dev.tkspring;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Utils {

    public static List<Class<?>> findClassesByAnnotation(Class<? extends Annotation> annotation, String... basePackages) throws Exception {
        List<Class<?>> output = new ArrayList<>();
        for (String basePackage : basePackages) {
            List<Class<?>> classes = findClasses(basePackage);
            for (Class<?> clazz : classes) {
                if (clazz.getAnnotation(annotation) != null && ! output.contains(clazz)) {
                    output.add(clazz);
                }
            }
        }
        return output;
    }

    private static List<Class<?>> findClasses(String packageName) throws Exception {
        String packagePath = packageName.replace('.', '/');
        List<Class<?>> classes = new ArrayList<>();
        File packageDir = new File(Thread.currentThread().getContextClassLoader().getResource(packagePath).toURI());
        for (File file : packageDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
            else if (file.isDirectory()) {
                classes.addAll(findClasses(packageName + "." + file.getName()));
            }
        }
        return classes;
    }
}

# Sprint 1: Détection des annotations

## Côté Framework
- Création de `Utils.findClassesByAnnotation`
- Création de `@Controller`
- Ajout de `FrontControllerServlet.init()` pour détecter les classes annotées `@Controller`

## Côté Test
- Dans `web.xml`:
```xml
<!-- ... -->
<servlet>
    <servlet-name>front-controller</servlet-name>
    <servlet-class>dev.tkspring.FrontControllerServlet</servlet-class>
    <init-param>
        <param-name>base-package</param-name>
        <param-value>your.test.package;another.test.package</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>front-controller</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
<!-- ... -->
```

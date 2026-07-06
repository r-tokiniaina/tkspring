# Sprint 4: ContextListener

## Côté Framework
- La détection des méthodes annotées `@Url` s’effectuent désormais dans un `ServletContextListener`

## Côté Test
- Dans `web.xml`:
```xml
<!-- ... -->
<context-param>
    <param-name>base-package</param-name>
    <param-value>your.test.package;another.test.package</param-value>
</context-param>

<servlet>
    <servlet-name>front-controller</servlet-name>
    <servlet-class>dev.tkspring.FrontControllerServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>front-controller</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
<!-- ... -->
```

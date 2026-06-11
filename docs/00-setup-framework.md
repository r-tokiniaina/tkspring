# Sprint 0: Setup du Framework

## Côté Framework
- Création de `FrontControllerServlet` qui capture toutes les requêtes GET et POST

## Côté Test
- Dans `web.xml`:
```xml
<!-- ... -->
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

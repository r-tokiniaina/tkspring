# Sprint 10: Authentification

## Côté Framework
- Création de l’annotation `@RequireAuth`
- Dans `FrontControllerServlet.processRequest()`, traiter l’annotation `@RequireAuth`

## Côté Test
- Dans `web.xml` (optionnel):
```xml
<!-- ... -->
<servlet>
    <servlet-name>front-controller</servlet-name>
    <servlet-class>dev.tkspring.FrontControllerServlet</servlet-class>
        <!-- ... -->
        <init-param>
           <param-name>auth-attr</param-name>
           <param-value>sessionAttrName</param-value>
        </init-param>
        <!-- ... -->
</servlet>
<!-- ... -->
```

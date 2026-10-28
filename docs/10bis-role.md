# Sprint 10 Bis: RBAC (Role-Based Access Control)

## Côté Framework
- Création de l’annotation `@RequireRoles`
- Dans `FrontControllerServlet.processRequest()`, traiter l’annotation `@RequireRoles`

## Côté Test
- Dans `web.xml` (optionnel):
```xml
<!-- ... -->
<servlet>
    <servlet-name>front-controller</servlet-name>
    <servlet-class>dev.tkspring.FrontControllerServlet</servlet-class>
        <!-- ... -->
        <init-param>
           <param-name>role-attr</param-name>
           <param-value>sessionAttrName</param-value>
        </init-param>
        <!-- ... -->
</servlet>
<!-- ... -->
```

# TKSpring
Un mini-framework, écrit en Java, qui s’inspirent fortement de Spring MVC.

## Prérequis
- Java
- Tomcat

## Setup
- Créer un projet Tomcat
- Mettre `tkspring.jar` dans `./WEB-INF/lib/`
- Mettre dans `web.xml` :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- ... -->
<context-param>
    <param-name>base-package</param-name>
    <param-value>packages.where.to.scan.controllers</param-value>
</context-param>

<servlet>
    <servlet-name>front-controller</servlet-name>
    <servlet-class>dev.tkspring.FrontControllerServlet</servlet-class>

    <init-param>
        <param-name>view-prefix</param-name>
        <param-value>/WEB-INF/views/</param-value>
    </init-param>

    <init-param>
        <param-name>view-suffix</param-name>
        <param-value>.jsp</param-value>
    </init-param>
</servlet>

<servlet-mapping>
    <servlet-name>front-controller</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
<!-- ... -->
```
- Penser à compiler avec: l’option `-parameters`
- Générer un fichier `ton-projet.war`
- Placer le fichier `ton-projet.war` dans `/path/to/tomcat/webapps`

## Utilisation
- Pour créer un controlleur :
```java
import dev.tkspring.annotation.Controller;

@Controller
public class MyController {
    // ...
}
```
- Pour ajouter une méthode d’action :
```java
import dev.tkspring.constant.HttpMethod;
import dev.tkspring.annotation.Url;

// Utilisation:
//     @Url(String value, HttpMethod method)
// Où:
//     method = HttpMethod.GET (par défaut) ou HttpMethod.POST
@Url("/path/to/this", method=HttpMethod.GET)
public void methode() {
    // ...
}
```
- Pour afficher une vue JSP :
```java
import dev.tkspring.ModelAndView;

@Url("/path/to/this")
public ModelAndView methode() {
    // Constructeur:
    //     ModelAndView(String view)
    // Méthodes:
    //     String getView()
    //     void setView(String view)
    //     java.util.Map<String, Object> getModel()
    //     Object get(String attr)
    //     void set(String attr, Object value)
    ModelAndView mav = new ModelAndView("nom_du_fichier_jsp_sans_prefixe_et_suffixe");
    mav.set("attr", "value");
    return mav;
}
```
- Pour renvoyer du JSON :
```java
import dev.tkspring.constant.HttpMethod;
import dev.tkspring.annotation.Url;

// Utilisation:
//     @AsJson(boolean raw)
// Où:
//     raw = true (la valeur renvoyée est déjà formatté en JSON) ou false (par défaut)
@Url("/path/to/this")
@AsJson
public java.util.Map<String, String> methode() {
    return java.util.Map.of("attr", "value");
}

@Url("/path/to/this")
@AsJson(raw = true)
public String methode() {
    return "{\"attr\": \"value\"}";
}
```
- Toutes les possibilités de paramètres d’une méthode d’action :
```java
@Url("/path/to/this")
public void methode(
    // Le type est identifié pour remplir ces paramètres
    jakarta.servlet.ServletContext context,
    jakarta.servlet.http.HttpServletResponse response,
    jakarta.servlet.http.HttpServletRequest request,

    // Le nom est comparé à un paramètre de requête
    // Le type est utilisé pour parser la valeur
    boolean arg1, char arg2,
    byte arg3, short arg4, int arg5, long arg6,
    float arg7, double arg8,
    Boolean arg9,
    Character arg10,
    Byte arg11, Short arg12, Integer arg13, Long arg14,
    Float arg15, Double arg16,
    BigInteger arg17, BigDecimal arg18,
    String arg19,
    Date arg20, LocalDate arg21, LocalDateTime arg22,
    String[] arg23 // ... et tous les précédents mais en tant que tableau
}
```

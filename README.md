# SISPRE — Ejecución Financiera (Título III)

Capa web del **Sistema para el Control y Seguimiento de la Ejecución Financiera de los Entes Descentralizados** (SISPRE), desarrollado para la **Oficina Nacional de Presupuesto (ONAPRE)** de Venezuela.

Este repositorio publica el **módulo web de ejecución (Título III)** como portafolio histórico: captura de saldos, modificaciones presupuestarias, ejecución mensual, cierre de mes, reportes y administración de usuarios por ente.

> **Alcance:** solo el *web tier* (JSF / Facelets). La lógica de negocio vive en EJBs externos del ecosistema ONAPRE (`ve.gob.onapre.*`) desplegados en el mismo servidor de aplicaciones.

---

## Qué hace el sistema

Tras autenticarse, el usuario selecciona el **ente / órgano** y trabaja sobre el ejercicio presupuestario activo:

| Módulo | Descripción |
|--------|-------------|
| **Saldo inicial de caja** | Registro del saldo de apertura por fuente / estructura |
| **Modificaciones de recursos** | Ajustes presupuestarios del lado de ingresos |
| **Modificaciones de egresos** | Ajustes presupuestarios del lado de gastos |
| **Ejecución mensual (recursos)** | Carga y seguimiento de la ejecución de ingresos del mes |
| **Ejecución mensual (egresos)** | Carga y seguimiento de la ejecución de gastos del mes |
| **Cierre de mes** | Cierre formal del período (roles ONAPRE / admin) |
| **Reportes** | Generación PDF con JasperReports |
| **Usuarios** | Administración de usuarios del módulo de ejecución |
| **Cambiar organismo** | Reasignación del ente en sesión |

El menú se renderiza según **roles JAAS** (`ADMIN`, `EJECUCION_TIII_ENTE`, `EJECUCION_TIII_ONAPRE`, `CONSULTA_EJEC_TIII`, `ADMIN_USER_EJEC_TIII`, etc.).

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| UI | JSF 2.1 (Mojarra), Facelets (XHTML), PrimeFaces 4.0 + Extensions, tema Redmond, Apache MyFaces Tomahawk |
| Contenedor | JBoss AS 7.1 / EAP (Eclipse Dynamic Web Project, Java 6, Servlet 3.0) |
| Seguridad | JAAS (`security-domain` `ONAPRE`), constraints en `web.xml` |
| Reportes | JasperReports 5 + iText / Barcode4J / Batik |
| Persistencia (vía EJB) | Oracle Database (driver JDBC **no incluido**; ver notas) |
| Idioma UI | Español (`faces-config.xml`) |

**Context root:** `/SIS_EJEC`  
**Entrada:** `faces/apps/seleccion/seleccion_ente.xhtml` (post-login) / `login/login.xhtml`

---

## Estructura del proyecto

```text
onapre-sispre-ejecucion-web/
├── src/                          # Managed beans, listeners, utilidades
│   ├── pagecode/apps/            # Beans por módulo (login, saldo, ejecución…)
│   ├── pagecode/template/        # MenuBean y navegación
│   ├── apps/onapre/sispre/       # ServletContextListener (precarga año fiscal)
│   └── atributes/                # Claves de sesión / application map
├── WebContent/
│   ├── apps/                     # Pantallas XHTML por módulo
│   ├── login/                    # Login + captcha
│   ├── templates/                # Layouts y menú Título III
│   ├── resources/                # CSS / JS / imágenes
│   └── WEB-INF/
│       ├── web.xml               # Faces Servlet, roles, session timeout
│       ├── faces-config.xml
│       ├── jboss-web.xml         # context-root + security-domain
│       └── lib/                  # Dependencias embebidas (sin ojdbc)
└── README.md
```

Arquitectura típica de la época: **WAR (esta app) + EARs/JARs EJB** en el mismo JBoss, con lookups JNDI (`LookUpNames`) hacia servicios locales.

---

## Requisitos para ejecutar (legado)

Este código **no es un proyecto Maven/Gradle autónomo**. Para levantarlo hace falta el entorno institucional original (o un equivalente):

1. **JDK 6/7** compatible con el runtime JBoss 7.1  
2. **JBoss AS 7.1** (o EAP 6.x) con el *security domain* `ONAPRE` configurado  
3. **Módulos EJB ONAPRE** desplegados (usuarios, estructura presupuestaria, ejecución Título III, comunes, etc.)  
4. **Datasource Oracle** referenciado por los EJBs (`CoDatabaseManager.ONAPRE`)  
5. Driver **Oracle JDBC** (`ojdbc`) en el servidor o en `WEB-INF/lib` — **no se redistribuye** en este repo por licencia Oracle  
6. Importar en **Eclipse** como *Dynamic Web Project* o empaquetar el WAR apuntando `WebContent` + `src`

Sin los EJBs y la base, la UI arranca de forma limitada: los `@EJB` / `@Resource` fallarán en runtime.

---

## Flujo funcional (alto nivel)

```text
Login (JAAS + CAPTCHA)
   → ¿clave por defecto? → cambio de clave
   → ¿varios entes? → selección de ente
   → menú Título III
        → saldo / modificaciones / ejecución / cierre / reportes / usuarios
```

Al iniciar la aplicación, `PreCargaDatos_SIS_EJEC` carga en el `ServletContext` el **año presupuestario activo**, rutas de reportes e histórico de años.

---

## Dependencias embebidas (`WEB-INF/lib`)

Incluidas (open source / runtime de reportes y UI):

- PrimeFaces 4.0, PrimeFaces Extensions, tema Redmond  
- Tomahawk 2.0  
- JasperReports 5.0.1 (+ fonts), iText, Barcode4J, Batik  
- Commons BeanUtils / Collections / Digester / Logging  
- Groovy, Xerces  

**Excluida a propósito:**

- `ojdbc14.jar` — driver Oracle; descárgalo desde Oracle y colócalo en `WebContent/WEB-INF/lib/` o en el módulo JDBC de JBoss.

---

## Notas de portafolio

- Proyecto **legacy** (~2015–2017): Eclipse + JBoss + JSF/PrimeFaces, típico del stack institucional de la época.  
- Demuestra dominio de: **presupuestación pública**, **roles/seguridad por ente**, **cierre mensual**, **reportes PDF** y **integración EJB**.  
- Publicado con fines de **portafolio / referencia histórica**. La marca ONAPRE y los derechos institucionales pertenecen a la Oficina Nacional de Presupuesto; este repositorio documenta el trabajo de desarrollo del módulo web.

---

## Autor

**Luis Guillermo Valles Velásquez** — desarrollo del módulo web de ejecución SISPRE (ONAPRE).

---

## Licencia

Código de portafolio. Sin licencia open source otorgada por ONAPRE en este repositorio: úsalo como referencia de arquitectura y experiencia, no como producto oficial redeployable.

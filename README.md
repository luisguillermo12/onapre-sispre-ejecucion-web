# SISPRE — Ejecución Financiera (Título III)

> **Proyecto de ejemplo / portafolio.**  
> Este repositorio existe para **visualizar y revisar el código** construido en el módulo web de ejecución financiera de SISPRE. No es un producto oficial redeployable ni un entorno listo para producción: es una muestra técnica del trabajo realizado en esa aplicación.

---

## Contexto

**SISPRE** (Sistema para el Control y Seguimiento de la Ejecución Financiera de los Entes Descentralizados) fue desarrollado para la **Oficina Nacional de Presupuesto (ONAPRE)** de Venezuela.

Lo que verás aquí es la **capa web del módulo de Ejecución (Título III)**: pantallas JSF, managed beans, menús por rol, reportes y el flujo de trabajo del usuario final. Sirve para entender cómo estaba armada la UI y cómo se conectaba al backend institucional.

---

## Qué puedes ver en este código

Tras el login, el usuario elige un **ente / órgano** y opera sobre el ejercicio presupuestario activo. Los módulos incluidos son:

| Módulo | Qué hace |
|--------|----------|
| Saldo inicial de caja | Apertura de saldos por fuente / estructura |
| Modificaciones de recursos | Ajustes presupuestarios de ingresos |
| Modificaciones de egresos | Ajustes presupuestarios de gastos |
| Ejecución mensual (recursos / egresos) | Carga y seguimiento de la ejecución del mes |
| Cierre de mes | Cierre formal del período |
| Reportes | Generación de PDF (JasperReports) |
| Usuarios | Administración de usuarios del módulo |
| Cambiar organismo | Cambio del ente en sesión |

La visibilidad del menú depende de roles JAAS (`ADMIN`, `EJECUCION_TIII_ENTE`, `EJECUCION_TIII_ONAPRE`, `CONSULTA_EJEC_TIII`, etc.).

---

## Stack

| Área | Tecnología |
|------|------------|
| UI | JSF 2.1 (Mojarra), Facelets (XHTML), PrimeFaces 4.0 + Extensions, tema Redmond, Tomahawk |
| Servidor | JBoss AS 7.1 / EAP · Eclipse Dynamic Web Project · Java 6 · Servlet 3.0 |
| Seguridad | JAAS (`security-domain` `ONAPRE`) + constraints en `web.xml` |
| Reportes | JasperReports 5, iText, Barcode4J, Batik |
| Datos (vía EJB) | Oracle Database |

**Context root:** `/SIS_EJEC`  
**Pantallas de entrada:** `login/login.xhtml` · `faces/apps/seleccion/seleccion_ente.xhtml`

---

## Importante sobre el alcance

Este repo contiene **solo el web tier**. La lógica de negocio vive en EJBs externos del ecosistema ONAPRE (`ve.gob.onapre.*`), desplegados junto a esta aplicación en JBoss.

En otras palabras:

- Aquí puedes **leer** pantallas, beans, navegación, roles y reportes.
- Para **ejecutarlo de punta a punta** harían falta esos EJBs, el datasource Oracle y la configuración institucional original.

---

## Estructura

```text
onapre-sispre-ejecucion-web/
├── src/                     # Managed beans, listeners y utilidades
│   ├── pagecode/apps/       # Beans por módulo (login, saldo, ejecución…)
│   ├── pagecode/template/   # Menú y navegación
│   ├── apps/onapre/sispre/  # Precarga del año presupuestario
│   └── atributes/           # Claves de sesión / application map
├── WebContent/
│   ├── apps/                # Pantallas XHTML
│   ├── login/               # Login + captcha
│   ├── templates/           # Layouts y menú Título III
│   ├── resources/           # CSS, JS e imágenes
│   └── WEB-INF/             # web.xml, faces-config, libs
└── README.md
```

Patrón típico de la época: **WAR (esta app) + módulos EJB** en el mismo servidor, con lookups JNDI (`LookUpNames`).

---

## Flujo funcional (resumen)

```text
Login (JAAS + CAPTCHA)
  → clave por defecto → cambio de clave
  → varios entes → selección de ente
  → menú Título III
       → saldo / modificaciones / ejecución / cierre / reportes / usuarios
```

Al arrancar, `PreCargaDatos_SIS_EJEC` carga en el `ServletContext` el año presupuestario activo, las rutas de reportes y el histórico de años.

---

## Cómo se desplegaba (referencia)

No es un proyecto Maven/Gradle autónomo. El entorno original incluía, a grandes rasgos:

1. JDK 6/7 + JBoss AS 7.1 (o EAP 6.x) con el security domain `ONAPRE`
2. Módulos EJB ONAPRE (usuarios, estructura presupuestaria, ejecución Título III, comunes…)
3. Datasource Oracle usado por los EJBs
4. Driver Oracle JDBC (`ojdbc`) en el servidor o en `WEB-INF/lib` — **no se incluye** en este repo por licencia
5. Importación en Eclipse como *Dynamic Web Project* (o empaquetado WAR de `WebContent` + `src`)

Sin los EJBs y la base, la UI no completa el ciclo: los `@EJB` / `@Resource` fallan en runtime. Eso es esperado en este repositorio de ejemplo.

---

## Dependencias en `WEB-INF/lib`

Incluidas (UI / reportes):

- PrimeFaces 4.0, Extensions y tema Redmond
- Tomahawk 2.0
- JasperReports 5.0.1 (+ fonts), iText, Barcode4J, Batik
- Commons BeanUtils / Collections / Digester / Logging
- Groovy, Xerces

Excluida:

- `ojdbc14.jar` (driver Oracle; obtenerlo desde Oracle si se necesita para un entorno local)

---

## Objetivo de este repositorio

- Mostrar código real de un sistema de **presupuestación pública** (ejecución financiera).
- Ilustrar un stack legacy típico: **JSF + PrimeFaces + EJB + JBoss + Oracle**.
- Servir como pieza de **portafolio** para revisar arquitectura, módulos y estilo de implementación.

La marca ONAPRE y los derechos institucionales pertenecen a la Oficina Nacional de Presupuesto. Este repo documenta el trabajo de desarrollo del módulo web con fines de ejemplo y referencia.

---

## Autor

**Luis Guillermo Valles Velásquez**  
Desarrollo del módulo web de ejecución SISPRE (ONAPRE).

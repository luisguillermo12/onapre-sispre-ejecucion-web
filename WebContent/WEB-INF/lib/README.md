# Librerías

Las dependencias JAR del WAR viven en este directorio.

El driver **Oracle JDBC** (`ojdbc*.jar`) **no se incluye** en el repositorio por restricciones de licencia de Oracle.

Para desarrollo local:

1. Descarga el driver compatible (p. ej. ojdbc14 / ojdbc6) desde Oracle.
2. Cópialo aquí como `ojdbc14.jar` (o el nombre que use tu runtime).
3. Alternativa recomendada en JBoss 7: instalar el driver como módulo del servidor y referenciar el datasource desde los EJBs.

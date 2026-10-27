# AgroSierra API

Backend REST para la cooperativa agrícola AgroSierra. El proyecto usa Java 21, Spring Boot, Maven y MySQL.

## Requisitos

- JDK 21
- Maven Wrapper incluido (`mvnw.cmd` en Windows)
- MySQL 8.x (local o mediante Docker Compose)

## Ejecutar

Desde esta carpeta, en PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

La aplicación toma la conexión desde `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. No guardes credenciales reales en archivos versionados.

### Levantar MySQL con Docker Compose

1. Copia la plantilla de variables y cambia las contraseñas locales:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Inicia MySQL:

   ```powershell
   docker compose up -d mysql
   ```

3. En la misma terminal, configura las variables para la API y ejecútala:

   ```powershell
   $env:DB_URL = "jdbc:mysql://localhost:3306/agrosierra?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   $env:DB_USERNAME = "agrosierra_app"
   $env:DB_PASSWORD = "cambia_esta_clave_local"
   ./mvnw.cmd spring-boot:run
   ```

Al iniciar, Flyway aplica las migraciones de `src/main/resources/db/migration`. Hibernate valida el esquema y no lo modifica automáticamente.

Si ya tienes MySQL instalado, crea la base ejecutando `scripts/crear_base.sql`, configura las mismas tres variables de entorno con tu usuario y contraseña, y ejecuta el comando de Maven anterior.

## Organización del código

El código de la API se organiza por responsabilidad dentro de `src/main/java/pe/edu/agrosierra/agroapi_Baldeon/`:

- `config`: configuración transversal de la aplicación y seguridad.
- `seguridad`: usuarios, roles, privilegios y autenticación.
- `dominio`: entidades y conceptos del negocio AgroSierra.
- `repositorio`: acceso y persistencia de datos.
- `servicio`: operaciones de negocio y reglas RN-01 a RN-04.
- `web`: controladores REST y manejo HTTP.
- `web/dto`: objetos de entrada y salida del API.

Los controladores deben delegar las reglas de negocio a `servicio`; la autorización se implementará en el backend.

## Esquema inicial

La migración `V1__create_initial_schema.sql` crea las tablas de usuarios, roles, privilegios, lotes y labores, junto con sus relaciones e índices. Los datos semilla se agregarán en una etapa posterior.

## Versionado de la API

Las rutas del servicio se implementarán bajo `/api/v1` conforme al contrato del examen.

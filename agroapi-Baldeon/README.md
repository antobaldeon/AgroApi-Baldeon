# AgroSierra API

Backend REST para la cooperativa agrícola AgroSierra. El proyecto usa Java 21, Spring Boot, Maven y MySQL; implementaremos sus funcionalidades por etapas.

## Requisitos

- JDK 21
- Maven Wrapper incluido (`mvnw.cmd` en Windows)
- MySQL se configurará en la siguiente etapa

## Ejecutar

Desde esta carpeta, en PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

La configuración local de la base de datos todavía está pendiente. No agregues credenciales al repositorio; en la etapa de MySQL se configurarán mediante variables de entorno.

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

## Versionado de la API

Las rutas del servicio se implementarán bajo `/api/v1` conforme al contrato del examen.

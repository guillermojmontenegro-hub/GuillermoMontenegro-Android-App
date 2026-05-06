# Tests

Este documento resume los tests automáticos que existen hoy en el proyecto y qué validan.

## Panorama actual

Hoy el repositorio tiene:

- 21 tests locales/unitarios
- 4 tests instrumentados/de integración
- cobertura distribuida entre `app`, `core/data`, `feature/articles` y `feature/users`

No se encontraron tests automáticos en `core:model`, `core:ui`, `feature:profile` ni `feature:onboarding`.

## Tipos de test

### Tests locales

Corren en la máquina de desarrollo con JUnit y `kotlinx-coroutines-test`.

Módulos con tests locales:

- `app/src/test`
- `core/data/src/test`
- `feature/articles/src/test`
- `feature/users/src/test`

### Tests instrumentados / integración

Corren en emulador o dispositivo Android real.

Ubicación:

- `app/src/androidTest`

Estos tests usan:

- `ActivityScenario` para lanzar la app real
- `UiAutomator` para interactuar con la UI visible

## Inventario de tests

### `app/src/test`

Archivo: `app/src/test/java/com/example/cvguillermomontenegro/ExampleUnitTest.kt`

Qué valida:

- test de ejemplo de JUnit (`2 + 2 = 4`)

Archivo: `app/src/test/java/com/example/cvguillermomontenegro/MainActivityStateResolverTest.kt`

Qué valida:

- `findActiveUser()` devuelve el único usuario marcado como activo
- `findActiveUser()` devuelve `null` si no hay usuarios activos

### `core/data/src/test`

Archivo: `core/data/src/test/java/com/example/cvguillermomontenegro/data/repository/ArticleRepositoryTest.kt`

Qué valida:

- `ArticleRepository.getArticles()` mapea `ArticleDto` a modelo de dominio
- `ArticleRepository.getArticle(slug)` devuelve el artículo mapeado correctamente

### `feature/articles/src/test`

Archivo: `feature/articles/src/test/java/com/example/cvguillermomontenegro/ui/articles/ArticlesViewModelTest.kt`

Qué valida:

- al inicializar, `ArticlesViewModel` carga artículos y expone tags ordenados
- el filtro por búsqueda y tag reduce correctamente la lista visible
- ante error del caso de uso, el estado expone `error` y deja de cargar

Archivo: `feature/articles/src/test/java/com/example/cvguillermomontenegro/ui/articles/ArticleDetailViewModelTest.kt`

Qué valida:

- al inicializar, `ArticleDetailViewModel` carga el artículo a partir del `slug`
- ante error al obtener el artículo, expone `error` y no deja artículo cargado

### `feature/users/src/test`

Archivo: `feature/users/src/test/java/com/example/cvguillermomontenegro/ui/users/PhoneNumberFormatterTest.kt`

Qué valida:

- normalización de teléfono eliminando caracteres no numéricos
- formateo final esperado para números argentinos
- conversión de offsets entre valor original y valor transformado

Archivo: `feature/users/src/test/java/com/example/cvguillermomontenegro/ui/users/UserViewModelTest.kt`

Qué valida:

- se crea un usuario por defecto cuando el repositorio arranca vacío
- una carga inválida de formulario muestra errores y no persiste cambios
- al guardar, se recortan espacios y se formatea el teléfono antes de persistir
- al borrar el usuario activo, otro usuario pasa a ser activo
- `loadUser(id)` llena el formulario con un usuario existente
- el cambio de dark mode se persiste
- `selectActiveUser()` marca solo un usuario como activo
- `loadUser(id)` resetea el formulario si el usuario no existe

### `app/src/androidTest`

Archivo: `app/src/androidTest/java/com/example/cvguillermomontenegro/ExampleInstrumentedTest.kt`

Qué valida:

- el contexto instrumentado resuelve el package correcto de la app

Archivo: `app/src/androidTest/java/com/example/cvguillermomontenegro/SplashScreenIntegrationTest.kt`

Qué valida:

- limpia `SharedPreferences`
- lanza `MainActivity`
- espera que, después del splash, aparezca onboarding

Archivo: `app/src/androidTest/java/com/example/cvguillermomontenegro/UsersFlowIntegrationTest.kt`

Qué valida:

- limpia preferencias y base local
- recorre onboarding completo
- abre el drawer
- navega a la pantalla de usuarios
- entra al formulario de edición de usuario

Archivo: `app/src/androidTest/java/com/example/cvguillermomontenegro/ArticlesFlowIntegrationTest.kt`

Qué valida:

- limpia preferencias y base local
- recorre onboarding completo
- entra desde Home a la librería de artículos
- abre un artículo real
- verifica la navegación a la pantalla de detalle

## Cobertura real hoy

La suite actual cubre principalmente:

- sanidad básica del módulo `app`
- resolución de usuario activo al arrancar
- mapeo de repositorio de artículos
- lógica de estado y errores en ViewModels de artículos
- validación y persistencia de usuarios desde `UserViewModel`
- formateo de teléfono
- flujos instrumentados de splash, onboarding, usuarios y artículos

La suite todavía no cubre de forma automática:

- tests de Room con base real instrumentada
- tests de UI de Compose con asserts sobre nodos semánticos
- tests para `feature:profile`
- tests para `feature:onboarding`
- tests para `core:ui` y `core:model`

## Cómo ejecutarlos

### Todos los tests locales

```bash
./gradlew testDebugUnitTest
```

### Tests locales por módulo

```bash
./gradlew :app:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest
./gradlew :feature:articles:testDebugUnitTest
./gradlew :feature:users:testDebugUnitTest
```

### Tests instrumentados

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Requisitos para tests instrumentados

Para correr los tests instrumentados hace falta:

- un emulador o dispositivo Android disponible
- `adb` operativo
- build `debug` e `androidTest` compilables

Comandos útiles:

```bash
adb devices
./gradlew :app:assembleDebug
./gradlew :app:assembleDebugAndroidTest
./gradlew :app:connectedDebugAndroidTest
```

# Plan de migracion a Android

Origen relevado: `/mnt/ssd_storage/ParaAgentes/PaginaCurriculum`

Destino: `/home/guillermo/AndroidStudioProjects/CVGuillermoMontenegro2`

## Estado actual relevado

### Proyecto web origen

- Sitio personal en `Next.js 15`, `React 19` y `TypeScript`.
- Home de CV alimentada por `content/profile/profile.es.json` y `content/profile/profile.en.json`.
- Biblioteca de articulos alimentada por carpetas `Articulos/<slug>/index.md`.
- Cada articulo tiene frontmatter con `slug`, `title`, `date`, `description`, `author`, `tags` y `previewImageUrl`.
- Hay 7 articulos detectados:
  - `new-paradigm-sketch`
  - `tool-calling`
  - `pasado-presente-y-futuro-llms`
  - `SROW`
  - `model-as-function`
  - `basic-handbook-local-LM`
  - `deterministic-LLM`
- La web tiene estas superficies principales:
  - perfil profesional
  - experiencia
  - skills
  - proyectos
  - educacion
  - idiomas
  - contacto
  - listado de articulos
  - busqueda de articulos
  - detalle de articulo por slug
  - selector `es/en`

### Proyecto Android destino

- Ya tiene base Android con Kotlin, Jetpack Compose, Navigation Compose, Room, Hilt y KSP.
- Ya existe `CVApplication` con `@HiltAndroidApp`.
- Ya existe `MainActivity` con `@AndroidEntryPoint`, pero solo muestra un `Greeting`.
- Ya existe una base de Room para usuarios:
  - `UserEntity`
  - `UserDao`
  - `AppDatabase`
  - `UserRepository`
  - `UserViewModel`
  - `DatabaseModule`
- El usuario hoy solo soporta listado e insercion. Falta completar ABM real: detalle, edicion, baja, validaciones y pantallas Compose.
- Falta agregar Retrofit y la capa remota de articulos.

## Arquitectura objetivo

Mantener una arquitectura simple por capas, compatible con el tamano actual del proyecto:

```txt
app/src/main/java/com/example/cvguillermomontenegro/
  core/
    Result.kt
    UiText.kt
  data/
    local/
      AppDatabase.kt
      UserDao.kt
      UserEntity.kt
    remote/
      ArticleApi.kt
      ArticleDto.kt
      MockArticleInterceptor.kt
    repository/
      ArticleRepository.kt
      UserRepository.kt
  di/
    DatabaseModule.kt
    NetworkModule.kt
    RepositoryModule.kt
  domain/
    model/
      Article.kt
      Profile.kt
      User.kt
  ui/
    navigation/
      AppNavHost.kt
      Routes.kt
    screens/
      home/
      articles/
      users/
    theme/
```

## Decisiones de migracion

- Room se usa solo para usuarios.
- Los articulos no se persisten en Room por ahora.
- Los articulos deben llegar por Retrofit aunque esten mockeados.
- El mock recomendado para desarrollo es un `OkHttp Interceptor` que responda JSON local ante rutas como `/articles` y `/articles/{slug}`. De esa manera la app usa Retrofit real sin depender de un backend externo.
- El contenido del perfil puede quedar como assets JSON locales o como modelos Kotlin estaticos. Para mantener paridad con el origen, conviene copiar `profile.es.json` y `profile.en.json` a `app/src/main/assets/profile/`.
- Los articulos se pueden convertir desde Markdown/frontmatter a un JSON mock versionado en `app/src/main/assets/mock/articles.json`.
- Las previews pueden resolverse en una segunda pasada:
  - opcion simple: copiar previews a `res/drawable` o `assets/article_previews/` y mapearlas por slug;
  - opcion futura: servir URLs reales cuando exista backend.

## Plan por fases

### Fase 1 - Preparar dependencias y permisos

- Agregar Retrofit.
- Agregar converter JSON, preferentemente Moshi o Kotlinx Serialization.
- Agregar OkHttp y logging interceptor para debug.
- Agregar Coil si las previews se van a cargar como imagenes.
- Agregar `android.permission.INTERNET` al `AndroidManifest.xml` si se usa base URL HTTP/HTTPS real o futura.
- Mantener Room, Hilt, Navigation Compose y Material 3 como base existente.

### Fase 2 - Migrar contenido base

- Copiar `content/profile/profile.es.json` y `content/profile/profile.en.json` a assets.
- Definir modelos de dominio `Profile`, `ExperienceItem`, `EducationItem`, `LanguageItem`, `ContactData`.
- Convertir los `Articulos/<slug>/index.md` a mock JSON con metadata y contenido Markdown.
- Preservar los slugs originales porque son la identidad navegable del articulo.
- Copiar previews y thumbnails con una convencion estable por slug.

### Fase 3 - Completar ABM de usuarios con Room

- Expandir `UserDao` con:
  - `getUserById(id)`
  - `insertUser(user)`
  - `updateUser(user)`
  - `deleteUser(user)`
  - `deleteUserById(id)`
- Revisar si `UserEntity` necesita mas campos que `name` y `email`, por ejemplo `role`, `phone`, `createdAt`, `updatedAt`.
- Agregar modelo de dominio `User` y mappers `UserEntity <-> User` si la UI no debe depender de Room.
- Completar `UserRepository` con operaciones CRUD.
- Completar `UserViewModel` con estado de formulario, seleccion, guardado, edicion, borrado y errores de validacion.
- Crear pantallas Compose:
  - listado de usuarios
  - formulario alta/edicion
  - dialogo de confirmacion de baja
  - estado vacio

### Fase 4 - Implementar articulos via Retrofit mock

- Crear `ArticleApi`.
- Crear `ArticleDto` y mappers a `Article`.
- Crear `MockArticleInterceptor` para responder:
  - `GET /articles`
  - `GET /articles/{slug}`
- Crear `NetworkModule` con `OkHttpClient`, `Retrofit` y `ArticleApi`.
- Crear `ArticleRepository`.
- Crear `ArticlesViewModel` con:
  - carga inicial
  - loading
  - error
  - lista completa
  - busqueda por titulo/descripcion
  - filtro por tags
- Crear `ArticleDetailViewModel` o resolver el detalle desde el repositorio por `slug`.

### Fase 5 - Construir UI Compose del CV

- Reemplazar `Greeting` en `MainActivity` por `AppNavHost`.
- Crear rutas:
  - `home`
  - `articles`
  - `articleDetail/{slug}`
  - `users`
  - `userForm`
- Home:
  - hero con nombre, headline y resumen
  - experiencia
  - skills
  - proyectos
  - educacion
  - idiomas
  - contacto
  - acceso a articulos
  - acceso al ABM de usuarios
- Articulos:
  - listado ordenado por fecha descendente
  - buscador
  - chips de tags
  - tarjetas con preview, titulo, fecha y descripcion
- Detalle:
  - titulo
  - fecha
  - autor
  - tags
  - preview
  - contenido Markdown como texto inicial o renderer Markdown en una fase posterior
- Usuarios:
  - pantalla administrativa separada del perfil publico
  - alta, baja y modificacion persistidas en Room

### Fase 6 - Tema, i18n y calidad visual

- Crear un tema Material 3 propio para el CV.
- Mantener copy en espanol como default.
- Preparar soporte `es/en` con un estado de idioma en memoria o DataStore en una fase futura.
- Cuidar layouts responsive para phone y tablet con `LazyColumn`, `LazyVerticalGrid` cuando corresponda y superficies Material 3.
- Agregar iconos donde ayuden a acciones: editar, borrar, guardar, buscar, volver.

### Fase 7 - Pruebas y validacion

- Tests unitarios de `UserDao` con Room in-memory.
- Tests unitarios de `UserRepository`.
- Tests de mappers de articulos.
- Tests del `MockArticleInterceptor` o repositorio remoto con respuestas mock.
- Tests de ViewModels para estados de carga, error y operaciones de usuario.
- Ejecutar `./gradlew test`.
- Ejecutar `./gradlew connectedAndroidTest` si hay dispositivo/emulador disponible.
- Ejecutar `./gradlew assembleDebug`.

## Checklist de implementacion

### Preparacion

- [x] Confirmar campos definitivos del usuario para el ABM.
- [x] Definir si el perfil se lee desde assets JSON o queda modelado en Kotlin.
- [x] Definir si el detalle de articulos renderiza Markdown real o texto plano en la primera version.
- [x] Agregar dependencias Retrofit/OkHttp/converter JSON.
- [x] Agregar Coil si se usan previews como imagenes.
- [x] Agregar permiso `INTERNET` si corresponde.

### Contenido

- [x] Copiar perfiles `profile.es.json` y `profile.en.json` a assets.
- [x] Crear modelos de dominio de perfil.
- [x] Crear lector/repositorio de perfil.
- [x] Convertir los 7 articulos a JSON mock.
- [x] Copiar previews/thumbs con convencion por slug.
- [x] Validar que todos los articulos tengan titulo, fecha, descripcion, autor y tags.

### Usuarios Room

- [x] Completar `UserDao` con update/delete/getById.
- [x] Completar `UserRepository` con CRUD.
- [x] Definir modelo de dominio `User`.
- [x] Agregar mappers usuario.
- [x] Completar `UserViewModel`.
- [x] Crear pantalla de listado de usuarios.
- [x] Crear formulario de alta/edicion.
- [x] Crear confirmacion de baja.
- [x] Validar nombre y email antes de guardar.
- [ ] Probar persistencia cerrando y reabriendo la app.

### Articulos Retrofit

- [x] Crear DTOs de articulos.
- [x] Crear `ArticleApi`.
- [x] Crear `MockArticleInterceptor`.
- [x] Crear `NetworkModule`.
- [x] Crear `ArticleRepository`.
- [x] Crear mappers DTO a dominio.
- [x] Crear ViewModel de articulos.
- [x] Implementar loading/error/empty states.
- [x] Implementar busqueda.
- [x] Implementar filtros por tags.
- [x] Implementar detalle por slug.

### Navegacion y UI

- [x] Crear `Routes`.
- [x] Crear `AppNavHost`.
- [x] Conectar `MainActivity` al nav host.
- [x] Crear pantalla home del CV.
- [x] Crear pantalla biblioteca de articulos.
- [x] Crear pantalla detalle de articulo.
- [x] Crear pantalla ABM usuarios.
- [x] Agregar acciones de contacto con intents para email/web.
- [x] Agregar back navigation consistente.
- [x] Revisar estados vacios y errores.

### QA

- [ ] Ejecutar `./gradlew test`.
- [x] Ejecutar `./gradlew assembleDebug`.
- [ ] Revisar manualmente home.
- [ ] Revisar manualmente listado de articulos.
- [ ] Revisar manualmente detalle de al menos 2 articulos.
- [ ] Revisar alta de usuario.
- [ ] Revisar edicion de usuario.
- [ ] Revisar baja de usuario.
- [ ] Revisar rotacion/cambio de configuracion si aplica.

## Criterios de aceptacion iniciales

- La app abre en una home Compose con el CV migrado.
- El ABM de usuarios permite crear, listar, editar y borrar usuarios persistidos en Room.
- Los articulos se cargan desde una interfaz Retrofit.
- El origen de articulos esta mockeado dentro de la app.
- El listado de articulos permite buscar y filtrar.
- El detalle de articulo se abre por slug.
- El proyecto compila con `./gradlew assembleDebug`.

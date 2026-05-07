# CV Guillermo Montenegro

Construí esta aplicación Android nativa como una versión móvil de mi portfolio y como una pieza de software pensada para mostrar cómo trabajo. No quise hacer solo una app "linda" con mi CV: la idea fue usar el contenido profesional como excusa para demostrar decisiones de arquitectura, integración entre capas, criterios de mantenibilidad y herramientas actuales del ecosistema Android.

La app combina dos objetivos:

- presentar mi perfil, experiencia, proyectos, artículos técnicos y contacto
- exhibir una base técnica moderna con UI declarativa, persistencia local, consumo remoto mockeado e inyección de dependencias

## Documentación

- [Tests automáticos](docs/Tests.md)


## Video

Demo en funcionamiento de la app:

[Video](https://www.youtube.com/shorts/qCywnFRtl78).

## Qué quise mostrar con esta app

Quise que el proyecto funcione como una demo técnica relativamente realista. Por eso no dejé la app reducida a pantallas estáticas, sino que sumé navegación, onboarding, carga de contenido, detalle de artículos, ABM de usuarios y persistencia local.

En concreto, esta app me sirve para mostrar:

- cómo construyo interfaces con Jetpack Compose
- cómo organizo una app por capas sin sobrediseñarla
- cómo modularizo lógica de acceso mediante casos de uso
- cómo manejo estado de pantalla con `ViewModel` y `Flow`
- cómo conecto una capa remota con `Retrofit` y `OkHttp`
- cómo centralizo logs de app y red con `Timber`
- cómo persisto datos locales con `Room`
- cómo inyecto dependencias con `Hilt`
- cómo inspecciono problemas de memoria con `LeakCanary`
- cómo desacoplo contenido, UI e infraestructura para que la app pueda crecer

## Qué hace hoy la aplicación

Actualmente la app incluye:

- una home con perfil profesional, resumen, skills, experiencia, proyectos, educación, idiomas y contacto
- onboarding inicial para explicar el recorrido y las tecnologías que se muestran
- navegación principal con drawer accesible desde el avatar superior derecho
- animaciones de interacción en el avatar del drawer y en el FAB de artículos para reforzar la transición entre acciones y pantallas
- botón flotante para abrir la biblioteca técnica
- listado de artículos con búsqueda y detalle
- render de Markdown real en el detalle de artículos
- ABM de usuarios con alta, edición y baja
- persistencia local de usuarios en base de datos `Room`
- carga de perfil y artículos desde assets y una capa remota mockeada
- logging base de aplicación y red centralizado con `Timber`

## Stack y por qué lo usé

### Jetpack Compose

Elegí `Jetpack Compose` porque hoy es la forma más sólida de construir UI moderna en Android. En esta app lo uso para:

- definir toda la interfaz de forma declarativa
- componer pantallas, componentes reutilizables y estados visuales sin XML
- montar navegación, drawer desde avatar, onboarding, FAB animado, microinteracciones de click y formularios
- iterar rápido sobre estética y estructura sin tener que mantener árboles de vistas tradicionales

También me interesaba que la app mostrara cómo Compose escala más allá de una pantalla simple: acá hay navegación, pantallas con scroll, formularios, drawer accionado desde un avatar, componentes reutilizables, microanimaciones de interacción y un shell visual compartido.

### Material 3

Uso `Material 3` como base visual para no inventar un sistema desde cero. Me da:

- componentes consistentes
- color system y surfaces ordenadas
- drawer, top bar, avatar de usuario, chips, cards, diálogos y FABs
- una base razonable para ajustar identidad visual sin pelearme con detalles básicos de accesibilidad y comportamiento

### Navigation Compose

Con `Navigation Compose` resolví el flujo entre onboarding, home, artículos, detalle y ABM de usuarios. Me interesa mostrar una navegación simple, explícita y fácil de seguir. No quise esconder lógica de rutas detrás de demasiada abstracción.

Además, acompañé algunas acciones clave con animaciones breves en Compose. El avatar del usuario que abre o cierra el drawer hace una transición de escala y rotación, y el FAB de artículos acentúa el click antes de navegar a la biblioteca. La intención no fue cargar la UI de motion, sino usar animación puntual para reforzar causalidad entre gesto y destino.

### ViewModel, Coroutines y Flow

La app usa `ViewModel` para conservar estado de pantalla y separar lógica de UI. Con `Coroutines` y `Flow` manejo:

- listado reactivo de usuarios desde Room
- carga de perfil
- carga y filtrado de artículos
- estado de formularios
- estados de loading y error

Mi intención acá fue mantener una solución idiomática de Android moderno sin sobreingeniería.

### Casos de uso

Agregué casos de uso dentro de cada feature module para modularizar operaciones concretas y evitar que los `ViewModel` dependan directo de los repositorios. Hoy, por ejemplo, la carga de perfil vive en `feature:profile`, la carga de artículos vive en `feature:articles` y las operaciones del ABM viven en `feature:users`.

Esto me interesa por dos motivos:

- hace más explícita la intención de cada operación
- deja un punto intermedio claro entre UI y datos para crecer reglas de negocio sin ensuciar los `ViewModel`
- mantiene cada feature desacoplada de las demás, compartiendo solo contratos y servicios de `core`

En este proyecto lo usé de forma pragmática: no como ceremonia, sino como una capa liviana dentro de cada módulo funcional para encapsular comportamientos concretos.

### Room

El ABM de usuarios está implementado con `Room`. Lo usé para mostrar persistencia local real, no solo estado en memoria. Esto permite:

- guardar usuarios en base local
- listarlos de forma reactiva
- editarlos y borrarlos
- comprobar que sobreviven al cierre y reapertura de la app

Además, me sirve para mostrar la separación entre entidad local, modelo de dominio, DAO, repositorio y ViewModel.

### Retrofit, OkHttp y Moshi

Para los artículos usé `Retrofit` con `OkHttp` y `Moshi`. Aunque hoy la fuente remota está mockeada dentro de la app, la intención fue respetar un flujo de red real:

- `Retrofit` define la interfaz del servicio
- `OkHttp` maneja el cliente HTTP
- un `Interceptor` mock responde `/articles` y `/articles/{slug}`
- `Moshi` convierte DTOs a objetos Kotlin

Hice esto así porque quería demostrar una integración remota desacoplada del backend definitivo. Si mañana reemplazo el mock por un servicio real, la UI no debería enterarse demasiado.

### Timber

Sumé `Timber` como sistema de logging base para evitar depender de llamadas dispersas a `Log.*` y dejar un punto único para observar lo que hace la app.

Hoy lo uso para:

- inicializar logging global desde `Application`
- centralizar logs manuales de la app
- redirigir el `HttpLoggingInterceptor` de `OkHttp` al mismo flujo de logs

Esto me deja la puerta abierta para endurecer el comportamiento por entorno, filtrar categorías o enviar eventos a otra herramienta más adelante sin reescribir todos los puntos de log.

### Kotlinx Serialization

Uso `kotlinx.serialization` para leer contenido local desde assets, especialmente el perfil profesional en JSON. Me resulta una opción liviana y clara para parseo de contenido estático dentro de la app.

### Hilt

`Hilt` me resuelve la inyección de dependencias sin ruido excesivo. En esta app lo uso para:

- proveer base de datos y DAOs
- proveer cliente HTTP, Retrofit y API
- inyectar repositorios y casos de uso
- mantener el wiring centralizado en módulos de DI

Me interesa mostrar que una app chica también puede beneficiarse de DI si se usa con criterio y sin convertir cada archivo en una fábrica manual.

### Coil

`Coil` está integrado para cargar imágenes desde assets y previews de artículos. Es una elección práctica, moderna y alineada con Compose.

### Compose Richtext

Para el detalle de artículos integré `compose-richtext` y así renderizo Markdown real en Compose. Esto me permitió evitar texto plano y mostrar una experiencia más cercana a una biblioteca técnica real.

### LeakCanary

Mantengo `LeakCanary` activo en `debug` porque me interesa que el proyecto también refleje prácticas de diagnóstico, no solo features. Si una navegación, pantalla o flujo de Compose empieza a retener memoria de forma incorrecta, quiero tener visibilidad temprana del problema.

En este proyecto además dejé explícito el manejo de su dependencia para que siga funcionando aun cuando otras librerías traigan transitivas conflictivas.

Los logs textuales de `LeakCanary` se ven en `Logcat` filtrando por `LeakCanary`. Los heap dumps y reportes quedan, por defecto, en el almacenamiento interno de la app, dentro de `files/leakcanary/`, es decir en una ruta equivalente a `/data/user/0/com.example.cvguillermomontenegro/files/leakcanary/`.

## Arquitectura

La app quedó organizada como un proyecto multimódulo. `app` funciona como shell de composición, navegación e integración final; las features viven en módulos Gradle separados; y lo compartido se concentra en módulos `core`.

La estructura principal es:

- `app`: punto de entrada, `Application`, `MainActivity`, theme y navegación raíz
- `core:model`: modelos de dominio compartidos, como `User`, `Article` y `Profile`
- `core:data`: Room, Retrofit, repositorios, DTOs, DAOs, mappers y módulos Hilt de infraestructura
- `core:ui`: componentes Compose reutilizables y utilidades compartidas de UI
- `feature:profile`: home del CV, perfil profesional y caso de uso de carga de perfil
- `feature:articles`: listado, búsqueda, detalle Markdown y casos de uso de artículos
- `feature:users`: ABM de usuarios, formulario, ViewModel y casos de uso sobre Room
- `feature:onboarding`: experiencia inicial de uso y explicación técnica de la app

La regla de dependencia que sigo es simple:

- `app` conoce a las features para componer navegación
- las features dependen de `core`
- `core` no depende de ninguna feature
- una feature no depende de otra feature

Dentro de cada feature, los casos de uso modularizan operaciones como:

- obtención del perfil
- carga de artículos
- carga de detalle por slug
- listado de usuarios
- obtención de usuario por id
- guardado y borrado de usuarios

No quise convertir esto en una arquitectura ceremonial. Busqué una organización que fuera fácil de leer, suficientemente escalable y coherente con el tamaño actual del proyecto, pero con límites reales entre módulos para que cada feature pueda evolucionar de forma independiente.

## Origen de datos

Hoy la app trabaja con dos fuentes principales:

- `assets` para perfil, contenido base y recursos mock
- `Room` para los usuarios del ABM

Los artículos se obtienen mediante una interfaz remota con `Retrofit`, pero respondida por un `MockArticleInterceptor`. Esa decisión me permitió mostrar arquitectura de red sin depender todavía de un backend real.

## Decisiones que tomé a propósito

Hay algunas decisiones de implementación que fueron deliberadas:

- separar la app en feature modules desacoplados
- separar usuarios persistidos en `Room` de los artículos remotos
- usar assets versionados para el perfil y los mocks
- mantener el código bastante explícito en vez de esconder demasiada lógica detrás de abstracciones prematuras
- usar una navegación simple y visible para que el recorrido sea entendible
- dejar el onboarding como una forma de explicar tanto el uso como el valor técnico de cada sección

## Ejecución

Para correr el proyecto:

1. Abrí la carpeta en Android Studio.
2. Esperá la sincronización de Gradle.
3. Ejecutá la app en un emulador o dispositivo con Android 7.0+.

También puedo compilarlo por línea de comandos con:

```bash
./gradlew assembleDebug
```

## Tests

La documentación completa de pruebas está en [docs/Tests.md](docs/Tests.md).

Comandos principales:

```bash
./gradlew testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

### Recorrido automático para video

Además de los tests instrumentados de integración, el proyecto incluye un recorrido automático pensado para capturar un video de la app. Ese flujo:

- completa el onboarding con pausas visibles
- hace scroll por la home hasta contacto y vuelve
- abre la biblioteca de artículos
- entra al detalle de un artículo real
- vuelve a home
- abre el drawer desde el avatar superior derecho
- navega al ABM de usuarios
- entra al formulario de edición

El test está en `app/src/androidTest/java/com/example/cvguillermomontenegro/AppVideoShowcaseIntegrationTest.kt`.

Para ejecutarlo en un emulador o dispositivo conectado:

```bash
adb devices
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.cvguillermomontenegro.AppVideoShowcaseIntegrationTest
```

Para publicar la app, la idea es apoyarme en un flujo de CI/CD que mantenga el proceso más consistente y automatizable.

## Estado actual

Al momento de escribir este README, el proyecto compila y permite recorrer:

- onboarding
- home del CV
- biblioteca y detalle de artículos
- ABM de usuarios con persistencia local

Todavía hay margen para seguir puliendo detalles, tests y extensiones, pero la base ya está pensada como una muestra funcional de cómo desarrollo una app Android moderna.

## Cierre

Mi intención con este repositorio fue que el código diga algo más que "sé usar Android". Quise que muestre cómo pienso una app, cómo conecto UI, datos e infraestructura, y cómo priorizo claridad, mantenibilidad y criterio técnico incluso en un proyecto de presentación personal.

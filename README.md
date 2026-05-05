# CV Guillermo Montenegro

Construí esta aplicación Android nativa como una versión móvil de mi portfolio y como una pieza de software pensada para mostrar cómo trabajo. No quise hacer solo una app "linda" con mi CV: la idea fue usar el contenido profesional como excusa para demostrar decisiones de arquitectura, integración entre capas, criterios de mantenibilidad y herramientas actuales del ecosistema Android.

La app combina dos objetivos:

- presentar mi perfil, experiencia, proyectos, artículos técnicos y contacto
- exhibir una base técnica moderna con UI declarativa, persistencia local, consumo remoto mockeado e inyección de dependencias

## Qué quise mostrar con esta app

Quise que el proyecto funcione como una demo técnica relativamente realista. Por eso no dejé la app reducida a pantallas estáticas, sino que sumé navegación, onboarding, carga de contenido, detalle de artículos, ABM de usuarios y persistencia local.

En concreto, esta app me sirve para mostrar:

- cómo construyo interfaces con Jetpack Compose
- cómo organizo una app por capas sin sobrediseñarla
- cómo manejo estado de pantalla con `ViewModel` y `Flow`
- cómo conecto una capa remota con `Retrofit` y `OkHttp`
- cómo persisto datos locales con `Room`
- cómo inyecto dependencias con `Hilt`
- cómo inspecciono problemas de memoria con `LeakCanary`
- cómo desacoplo contenido, UI e infraestructura para que la app pueda crecer

## Qué hace hoy la aplicación

Actualmente la app incluye:

- una home con perfil profesional, resumen, skills, experiencia, proyectos, educación, idiomas y contacto
- onboarding inicial para explicar el recorrido y las tecnologías que se muestran
- navegación principal con drawer
- botón flotante para abrir la biblioteca técnica
- listado de artículos con búsqueda y detalle
- render de Markdown real en el detalle de artículos
- ABM de usuarios con alta, edición y baja
- persistencia local de usuarios en base de datos `Room`
- carga de perfil y artículos desde assets y una capa remota mockeada

## Stack y por qué lo usé

### Jetpack Compose

Elegí `Jetpack Compose` porque hoy es la forma más sólida de construir UI moderna en Android. En esta app lo uso para:

- definir toda la interfaz de forma declarativa
- componer pantallas, componentes reutilizables y estados visuales sin XML
- montar navegación, drawer, onboarding, FAB animado y formularios
- iterar rápido sobre estética y estructura sin tener que mantener árboles de vistas tradicionales

También me interesaba que la app mostrara cómo Compose escala más allá de una pantalla simple: acá hay navegación, pantallas con scroll, formularios, componentes reutilizables y un shell visual compartido.

### Material 3

Uso `Material 3` como base visual para no inventar un sistema desde cero. Me da:

- componentes consistentes
- color system y surfaces ordenadas
- drawer, top bar, chips, cards, diálogos y FABs
- una base razonable para ajustar identidad visual sin pelearme con detalles básicos de accesibilidad y comportamiento

### Navigation Compose

Con `Navigation Compose` resolví el flujo entre onboarding, home, artículos, detalle y ABM de usuarios. Me interesa mostrar una navegación simple, explícita y fácil de seguir. No quise esconder lógica de rutas detrás de demasiada abstracción.

### ViewModel, Coroutines y Flow

La app usa `ViewModel` para conservar estado de pantalla y separar lógica de UI. Con `Coroutines` y `Flow` manejo:

- listado reactivo de usuarios desde Room
- carga de perfil
- carga y filtrado de artículos
- estado de formularios
- estados de loading y error

Mi intención acá fue mantener una solución idiomática de Android moderno sin sobreingeniería.

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

### Kotlinx Serialization

Uso `kotlinx.serialization` para leer contenido local desde assets, especialmente el perfil profesional en JSON. Me resulta una opción liviana y clara para parseo de contenido estático dentro de la app.

### Hilt

`Hilt` me resuelve la inyección de dependencias sin ruido excesivo. En esta app lo uso para:

- proveer base de datos y DAOs
- proveer cliente HTTP, Retrofit y API
- inyectar repositorios en ViewModels
- mantener el wiring centralizado en módulos de DI

Me interesa mostrar que una app chica también puede beneficiarse de DI si se usa con criterio y sin convertir cada archivo en una fábrica manual.

### Coil

`Coil` está integrado para cargar imágenes desde assets y previews de artículos. Es una elección práctica, moderna y alineada con Compose.

### Compose Richtext

Para el detalle de artículos integré `compose-richtext` y así renderizo Markdown real en Compose. Esto me permitió evitar texto plano y mostrar una experiencia más cercana a una biblioteca técnica real.

### LeakCanary

Mantengo `LeakCanary` activo en `debug` porque me interesa que el proyecto también refleje prácticas de diagnóstico, no solo features. Si una navegación, pantalla o flujo de Compose empieza a retener memoria de forma incorrecta, quiero tener visibilidad temprana del problema.

En este proyecto además dejé explícito el manejo de su dependencia para que siga funcionando aun cuando otras librerías traigan transitivas conflictivas.

## Arquitectura

La estructura general del proyecto es esta:

- `ui`: pantallas, navegación, componentes reutilizables, theme y ViewModels de presentación
- `domain`: modelos de negocio que usa la app
- `data`: fuentes locales, fuentes remotas, repositorios y mappers
- `di`: módulos de inyección de dependencias

No quise convertir esto en una arquitectura ceremonial. Busqué una organización que fuera fácil de leer, suficientemente escalable y coherente con el tamaño actual del proyecto.

## Origen de datos

Hoy la app trabaja con dos fuentes principales:

- `assets` para perfil, contenido base y recursos mock
- `Room` para los usuarios del ABM

Los artículos se obtienen mediante una interfaz remota con `Retrofit`, pero respondida por un `MockArticleInterceptor`. Esa decisión me permitió mostrar arquitectura de red sin depender todavía de un backend real.

## Decisiones que tomé a propósito

Hay algunas decisiones de implementación que fueron deliberadas:

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

## Estado actual

Al momento de escribir este README, el proyecto compila y permite recorrer:

- onboarding
- home del CV
- biblioteca y detalle de artículos
- ABM de usuarios con persistencia local

Todavía hay margen para seguir puliendo detalles, tests y extensiones, pero la base ya está pensada como una muestra funcional de cómo desarrollo una app Android moderna.

## Cierre

Mi intención con este repositorio fue que el código diga algo más que "sé usar Android". Quise que muestre cómo pienso una app, cómo conecto UI, datos e infraestructura, y cómo priorizo claridad, mantenibilidad y criterio técnico incluso en un proyecto de presentación personal.

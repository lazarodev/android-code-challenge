# Decisiones Técnicas y Arquitectura - Catalogo Express

Este documento resume las decisiones de diseño, compensaciones y estrategias adoptadas para la prueba técnica.

## 1. Arquitectura y Estructura
Se implementó una arquitectura **MVI (Model-View-Intent)** en combinación con principios de **Clean Architecture**:
*   **MVI**: Manejo estricto del estado de la UI (`State`), flujo de intenciones del usuario (`Intent`) y efectos colaterales de un solo uso (`Effect`, ej: Toasts). Esto asegura un flujo de datos unidireccional (UDF) y predecible.
*   **Capa de Datos Limpia**: Separación clara entre modelos de red (`ProductResponse.kt`), base de datos (`ProductEntity.kt`) y dominio (`Product.kt`).
*   **Caché Offline (Single Source of Truth)**: La base de datos local (Room) actúa como la única fuente de verdad para la UI. Los datos remotos (Retrofit) se guardan en Room tras la llamada HTTP y la UI reacciona directamente al flujo de datos de Room.

## 2. Reto de Criterio: Normalización del Score
Fórmula requerida: `score = (rating * ln(stock + 1)) / max(price, 1)`
Para manejar datos nulos, de valor cero o cadenas vacías ("strings vacíos"), se adoptó la siguiente normalización estricta:
*   **Rating**: Si es nulo o menor que cero, se normaliza a `0.0`.
*   **Stock**: Si es nulo o menor que cero, se normaliza a `0`. Dado que `ln(0 + 1) = ln(1) = 0`, cualquier producto sin stock automáticamente tendrá un score de `0`.
*   **Price**: Si es nulo o menor que cero, se normaliza a `0.0`. El denominador utiliza `max(price, 1.0)`, garantizando que si el precio es inferior a 1 (como un precio normalizado a `0.0`), el divisor sea `1.0`. Esto previene divisiones por cero.
*   **Cadenas Vacías / Tipos Inválidos**: Se desarrollaron funciones de extensión (`toDoubleOrZero()`, `toIntOrZero()`) que convierten de forma segura cualquier objeto `Any?` (incluyendo cadenas vacías `""` o texto no numérico) en su correspondiente valor por defecto seguro (`0.0` o `0`).

## 3. Decisiones de Nube y Backend
*   **Manejo de Tokens (JWT)**: Para almacenar un token de sesión de forma segura contra ingeniería inversa o inspección física del dispositivo, usaríamos **EncryptedSharedPreferences** (Android Jetpack Security), que encripta claves y valores automáticamente a nivel de hardware mediante el Android KeyStore System (AES de 256 bits). El token se adjuntaría en las peticiones de forma automatizada mediante un interceptor de OkHttp añadiendo la cabecera `Authorization: Bearer <token>`.
*   **Resiliencia y Errores**:
    *   **Timeouts**: Configurado un tiempo límite de `15 segundos` en OkHttp.
    *   **Retries / Red**: Mapeo exhaustivo de excepciones de red (`IOException`) a una clase personalizada `NetworkException` y errores HTTP a `ServerException` (capturando códigos 4xx y 5xx) para mostrar mensajes e interfaces personalizadas de reintento.
*   **Estrategia de Release**:
    *   **Diferenciación de Ambientes**: Configurada la URL base (`BASE_URL`) e interceptores de log en OkHttp mediante flags condicionales de compilación (`BuildConfig.DEBUG`). El logging interceptor completo solo se añade en modo Debug.
    *   **Entrega a QA**: Implementaríamos un pipeline de CI/CD (GitHub Actions / Bitrise) que al realizar push a ramas de prueba compile un APK de debug y lo suba de forma automatizada a **Firebase App Distribution** o **Google Play internal sharing**, notificando al equipo de QA.

## 4. Tradeoffs (Compensaciones y/o mejoras)
*   **Manual DI vs Hilt/Koin**: Debido al uso de Kotlin 2.2.10 y las restricciones de versiones en tiempo de compilación del compilador de KSP, se optó por un **contenedor de dependencias manual** (`AppContainer`). Esto garantiza total estabilidad y velocidad de compilación sin agregar complejidad de plugins de gradle ni overhead en tiempo de ejecución.
*   **Room DAO Síncrono**: Para evitar bugs de JVM signature `V` (void) causados por KSP2 al procesar funciones de suspensión (`suspend`) que retornan implícitamente `Unit` en DAOs de Room, se definieron los métodos de escritura en Room como síncronos y se ejecutaron mediante `withContext(Dispatchers.IO)` en la capa de repositorio. Esto mantiene la asincronía y soluciona un problema crítico de compatibilidad del compilador de Android Gradle Plugin 9.2.
*   **MVI base pattern**: Dado que el patron `MVI` (Model view intent) necesita tener clases selladas para mantener una arquitectura limpia y escalable notaremos codigo repetitivo `(boilerplate)` especificamente en los contratos. Como una optimización a futuro es utilizar una clase abstracta como una base de interfaces para cada contrato pueda extender y sobrescribir funciones y comportamientos reutilizables.
*   **Jetpack compose nav**: Debido al uso de Jetpack compose y que este es un proyecto nuevo desde cero, no se considero necesario el uso de fragments dado que Google anuncio este año que no agregaran funcionalidades nuevas a los widgets standalone (incluyendo fragments) y el enfoque debe ser Compose. Sin embargo en dado momento sea necesario el uso de un fragment o suponiendo que este es un proyecto existente con fragments en uso y para hacer una migración hacia Compose podriamos utilizar `ComposeView` dentro los `XMLs/fragments`.
*   **Capa de seguridad**: Para asegurar la integridad de las peticiones HTTP desde el app podemos utilizar un archivo xml para tener un `whitelist` de los dominios que el app debe soportar. Asi como también los certificados SSL necesarios para que pueda hacer la comunicación debida entre el cliente-servidor.

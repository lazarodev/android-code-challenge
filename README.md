# Catalogo Express - Android Code Challenge

**Catalogo Express** es una aplicación nativa de Android desarrollada en **Kotlin** que implementa un catálogo de productos con soporte sin conexión (offline-first). Los datos de la aplicación se obtienen de la API pública de DummyJSON, se normalizan para calcular un puntaje (*score*) dinámico y se ordenan de forma descendente en una interfaz moderna construida completamente con **Jetpack Compose**.

---

## A. Descripción de la Aplicación

La aplicación consta de dos pantallas principales:
1.  **Listado de Items**: Muestra los productos con su imagen (cargada eficientemente con Coil), título, precio formateado, estado de disponibilidad en stock (con color indicador) y su respectiva puntuación (*score*). La lista se ordena automáticamente por score descendente. Cuenta con un **Banner de Alerta de Conexión** que se muestra dinámicamente si el dispositivo se queda sin internet.
2.  **Detalle del Item**: Muestra de forma detallada y simplificada verticalmente la información ampliada de un producto seleccionado (carrousel deslizable de imágenes, calificación promedio, precio, stock actual, estado y descripción detallada).

---

## B. Pasos de Compilación y Ejecución

### Prerrequisitos
*   **JDK 17** o superior instalado.
*   **Android SDK 37** instalado (necesario para compilar las versiones de dependencias del proyecto).

### Comandos de Consola (Gradle)

Para compilar y ejecutar las tareas del proyecto desde la terminal, utiliza los siguientes comandos (asegúrate de que tu variable `JAVA_HOME` apunte a tu instalación de Java compatible):

*   **Compilar versión de depuración (Debug APK)**:
    ```bash
    ./gradlew assembleDebug
    ```
*   **Compilar versión de producción (Release APK)**:
    ```bash
    ./gradlew assembleRelease
    ```
*   **Ejecutar pruebas unitarias (Unit Tests)**:
    ```bash
    ./gradlew test
    ```
*   **Limpiar caché y temporales de construcción**:
    ```bash
    ./gradlew clean
    ```

*Nota: Si tu terminal macOS experimenta problemas de ruta con la variable de entorno de Java de Android Studio, puedes anteponer la ruta de forma explícita:*
```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assembleDebug
```

---

## C. Versiones de Tecnologías Usadas

El proyecto utiliza **Gradle Kotlin DSL** y la gestión de dependencias centralizada mediante **Version Catalog (`libs.versions.toml`)**:

*   **compileSdk**: `37` (Android 15 Q / 16)
*   **targetSdk**: `36`
*   **minSdk**: `26`
*   **Kotlin**: `2.2.10`
*   **Android Gradle Plugin (AGP)**: `9.2.1`
*   **KSP (Kotlin Symbol Processing)**: `2.2.10-2.0.2`
*   **Room Database**: `2.6.1`
*   **Retrofit**: `2.11.0`
*   **OkHttp & Logging Interceptor**: `4.12.0`
*   **Coil (Carga de Imágenes)**: `2.6.0`
*   **Jetpack Navigation Compose**: `2.8.7`
*   **Lifecycle Runtime Compose**: `2.11.0`

---

## D. Decisiones Técnicas Clave

Para una explicación exhaustiva, consulta el archivo [DECISIONES.md](file:///Users/fernandolazaro/AndroidStudioProjects/Androidcodechallenge/DECISIONES.md) en la raíz del proyecto. Un resumen de las decisiones clave:

1.  **Arquitectura MVI (Model-View-Intent)**: Implementa un flujo unidireccional de datos (UDF) que hace que la UI sea predecible y reactiva a un único estado (`State`) manejado por el `ViewModel`.
2.  **Base de Datos como Fuente Única de Verdad**: Room actúa como caché local persistente offline. Al iniciar o refrescar, los datos de red obtenidos de la API con Retrofit se guardan en Room, y la interfaz de usuario se actualiza automáticamente reaccionando a los cambios de la base de datos.
3.  **Normalización y Tolerancia a Fallos del Score**:
    *   La fórmula utilizada es: `score = (rating * ln(stock + 1)) / max(price, 1)`.
    *   Se crearon funciones de extensión seguras para transformar nulos, números negativos o cadenas vacías (`""`) de la API en números válidos de caída (`0.0` o `0`). El divisor utiliza `max(price, 1.0)` para impedir divisiones entre cero de forma matemática.
4.  **Bypass de Firma JVM en KSP2**: Debido a una incompatibilidad temporal en la versión del procesador de Room con KSP2 sobre Kotlin 2.2.10, que genera firmas inválidas `V` (void) para funciones de suspensión que retornan `Unit` de forma implícita, se configuraron los métodos de escritura de Room de manera síncrona. La asincronía se maneja limpiamente en la capa del repositorio delegando al hilo `Dispatchers.IO`.
5.  **Inyección de Dependencias Manual (`AppContainer`)**: Se diseñó un registro de dependencias manual tipo contenedor para optimizar el tiempo de compilación y evitar dependencias de plugins externos de Hilt/KAPT susceptibles a incompatibilidades en la versión de Kotlin.

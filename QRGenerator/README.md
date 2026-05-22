This is a Kotlin Multiplatform project targeting Android, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

---
# QRGenerator - Gimnasio

## Descripción del Proyecto
Sistema de Control de Acceso con Códigos QR desarrollado como una aplicación multiplataforma para Android y Desktop (JVM). Este sistema está diseñado para modernizar y asegurar el acceso a gimnasios y centros deportivos, eliminando los inconvenientes de las credenciales físicas tradicionales, como la lentitud en horas pico y la susceptibilidad al fraude.

##  Características Principales
* **Escaneo Inteligente:** Autenticación de socios mediante códigos QR únicos vinculados a su cuenta.
* **Validación en Tiempo Real:** Verificación inmediata del estado de la membresía (Activo, Vencido, Suspendido).
* **Control de Accesos:** Registro automático de cada intento de entrada (concedido o denegado) en una bitácora.
* **Administración de Usuarios:** Gestión completa del catálogo de socios (alta, baja y modificación).
* **Exportación de Datos:** Generación de reportes de acceso en formato CSV para análisis externo.

## Arquitectura y Tecnologías
El proyecto implementa el patrón de diseño **MVVM** (Model-View-ViewModel) combinado con **Kotlin Multiplatform (KMP)** para compartir la lógica de negocio entre plataformas.

* **Interfaz de Usuario (UI):** Compose Multiplatform.
* **Navegación:** Voyager (navegación declarativa).
* **Base de Datos:** SQLite integrado con SQLDelight (ORM multiplataforma type-safe).
* **Manejo de Estados:** Lifecycle ViewModel Compose y Coroutines.
* **Lector QR:** QR-Kit para el acceso a la cámara y lectura de códigos.

## Desarrolladores
Proyecto desarrollado para la asignatura de Tópicos Avanzados de Programación por:
* Cristian Alexis Lara Chavez
* Santiago Magaña Martínez
* Luis Eduardo Alvarado Alvarado
* Edu Humberto Rosiles González


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
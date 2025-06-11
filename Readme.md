# 📱 Gestor de Tareas

Un gestor de tareas sencillo y eficiente desarrollado con Jetpack Compose para Android.

## ✨ Características

* **Gestión de Tareas:** Añade, edita y elimina tus tareas diarias.
* **Marcado de Tareas:** Marca tareas como completadas para llevar un registro de tu progreso.
* **Visibilidad Clara:** Las tareas completadas se visualizan con un estilo opaco para diferenciarlas fácilmente.
* **Interfaz Intuitiva:** Diseño limpio y moderno, fácil de usar.

## 🛠️ Desarrollo

Esta aplicación está construida enteramente con **Jetpack Compose**, el moderno kit de herramientas UI declarativo de Android.

### Arquitectura
Se emplea el patrón MVVM para gestionar el estado de las tareas y la lógica de negocio, lo que permite una clara separación de responsabilidades entre la UI y los datos.

### Componentes Clave
* **`MainActivity.kt`**: Punto de entrada de la aplicación y configuración de la navegación Compose.
* **`MainScreen.kt`**: La pantalla principal donde se listan todas las tareas.
* **`FormularioScreen.kt`**: Pantalla para añadir nuevas tareas.
* **`EditarTareaScreen.kt`**: Pantalla para modificar tareas existentes.
* **`TareaCard.kt`**: Un composable reutilizable que representa visualmente cada tarea en la lista, incluyendo el checkbox, el nombre, la descripción, la fecha y el botón de eliminar. Este componente maneja la lógica de opacidad para tareas completadas y la alineación de sus elementos internos.
* **`MainViewModel.kt`**: Contiene la lógica para la gestión de tareas (añadir, editar, eliminar, marcar como completada) y expone la lista de tareas a la UI.
* **`Tarea.kt`**: Clase de datos que define la estructura de una tarea (id, nombre, descripción, estado de completado, fecha de creación).

### Dependencias Utilizadas

* **Jetpack Compose UI & Material3**: Para construir la interfaz de usuario.
* **Compose Navigation**: Para la navegación entre pantallas.
* **ViewModel & Lifecycle**: Para la gestión del estado y el ciclo de vida.
* **Material Icons Extended**: Para iconos adicionales como `DeleteOutline`.
* **Kotlinx Coroutines**: Para manejo de concurrencia en el ViewModel.
* **Java Time API**: Para el manejo de fechas (utilizando `LocalDateTime` y `DateTimeFormatter`).

## 🚀 Funcionamiento

La aplicación arranca en la `MainScreen`, donde se muestra una lista unificada de todas las tareas.

* **Añadir Tarea:** Usa el botón flotante `+` para navegar al formulario de creación de tareas.
* **Editar Tarea:** Haz clic en cualquier tarjeta de tarea **pendiente** para ir a la pantalla de edición.
* **Marcar/Desmarcar Tarea:** Utiliza el checkbox dentro de cada tarjeta para alternar el estado de completado de la tarea.
* **Eliminar Tarea:** El icono de papelera a la derecha de cada tarjeta permite eliminar una tarea, con una confirmación previa.
* **Visualización de Completadas:** Las tareas completadas mantienen su checkbox marcado y toda la tarjeta se muestra con una opacidad reducida.

## ⚙️ Cómo Ejecutar el Proyecto

1.  Clona este repositorio:
    ```bash
    git clone [https://github.com/jadapache/gestor-tareas.git](https://github.com/jadapache/gestor-tareas.git)
    ```
2.  Abre el proyecto en Android Studio.
3.  Sincroniza el proyecto con los archivos Gradle.
4.  Ejecuta la aplicación en un emulador o dispositivo Android.

---

**Autor:** jadapache

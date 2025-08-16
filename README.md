# Squash Game v1.0

Este es un sencillo juego de Squash (similar al Ping Pong) desarrollado en Java utilizando la biblioteca Swing para la interfaz gráfica.

## Descripción

El juego consiste en un jugador que controla una raqueta en la parte inferior de la pantalla y compite contra una raqueta controlada por la CPU. El objetivo es evitar que la pelota pase tu raqueta. A medida que avanzas, el juego aumenta de nivel y velocidad, haciendo el desafío cada vez mayor.

## Características

- **Jugador vs. CPU**: Compite contra una inteligencia artificial básica.
- **Sistema de Puntuación**: Gana puntos cada vez que golpeas la pelota.
- **Niveles**: El juego incrementa su dificultad a medida que el jugador anota más puntos.
- **Efectos de Sonido**: Sonidos para colisiones y fin del juego.
- **Gráficos Sencillos**: Interfaz gráfica creada con Java Swing, utilizando GIFs animados para los fondos.
- **Reinicio del Juego**: Opción para volver a jugar al perder.

## ¿Cómo Jugar?

### Controles

- **Mover a la Izquierda**: Flecha Izquierda o Tecla 'A'.
- **Mover a la Derecha**: Flecha Derecha o Tecla 'D'.
- **Mover hacia Arriba/Abajo**: Flechas Arriba/Abajo o Teclas 'W'/'S'.

### Objetivo

Usa tu raqueta para golpear la pelota y evitar que llegue al borde inferior de la pantalla. Cada vez que golpeas la pelota, ganas puntos. ¡Intenta conseguir la mayor puntuación posible!

## ¿Cómo Ejecutar el Proyecto?

### Prerrequisitos

- Tener instalado el Kit de Desarrollo de Java (JDK).

### Compilación y Ejecución

1.  Abre una terminal en el directorio raíz del proyecto (`Squash/`).
2.  Compila los archivos `.java`. Este comando guardará los archivos `.class` en el directorio `bin/`.

    ```bash
    javac -d bin src/*.java
    ```

3.  Ejecuta el juego.

    ```bash
    java -cp bin pingPong
    ```

## Estructura del Proyecto

```
.
├── Assets/      # Contiene todos los recursos multimedia (imágenes y sonidos).
├── bin/         # Directorio de salida para los archivos compilados (.class).
├── src/         # Código fuente del juego (.java).
└── README.md    # Este archivo.
```

## Resumen del Código

-   `pingPong.java`: Es la clase principal que contiene toda la lógica del juego. Se encarga de renderizar los gráficos, controlar el movimiento de la pelota y las raquetas, gestionar la entrada del usuario, la puntuación y los sonidos.
-   `score.java`: Un componente que fue diseñado para mostrar la puntuación, pero que actualmente no se utiliza en la versión actual del juego.

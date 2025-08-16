# Squash Game v2.0

Un juego clásico de Squash (o Pong de un solo jugador) desarrollado en Java con Swing. Esta versión incluye mejoras significativas en la jugabilidad, la inteligencia artificial del oponente y el rendimiento general.

## Mejoras Recientes (v2.0)

Esta versión se ha refactorizado para ofrecer una experiencia de juego más fluida, justa y desafiante.

### 1. Física de Colisiones Refinada
- **Detección por Hitbox**: Se reemplazó la detección de colisiones por línea con una **intersección de rectángulos (`java.awt.Rectangle`)**. Esto crea un área de impacto realista para la pelota y las raquetas, eliminando los fallos donde la pelota parecía atravesar la raqueta.
- **Ángulo de Rebote Dinámico**: El ángulo de rebote de la pelota ahora depende del punto de impacto en la raqueta. Golpear la pelota con el centro de la raqueta produce un rebote más directo, mientras que los golpes en los bordes generan ángulos más pronunciados.
- **Aceleración Progresiva**: La pelota aumenta ligeramente su velocidad después de cada golpe, haciendo que el juego sea progresivamente más rápido y desafiante.

### 2. IA Predictiva Avanzada
- **Lógica Predictiva**: Se descartó el algoritmo de "distancia euclidiana" simple. La nueva IA **predice la trayectoria futura de la pelota**, calculando el punto exacto donde interceptará la línea de la raqueta de la CPU, incluso después de rebotar en las paredes.
- **Movimiento Suavizado (Human-Like)**: Para evitar movimientos robóticos e instantáneos, la IA utiliza una **Media Móvil Exponencial (EMA)**. Esto suaviza el seguimiento del objetivo, haciendo que la CPU tenga un comportamiento más natural y menos predecible.
- **Velocidad Adaptativa**: La velocidad máxima de la CPU está limitada y aumenta gradualmente a medida que sube el nivel, asegurando un desafío justo y creciente.

### 3. Optimización de Rendimiento y Hilos
- **Bucle de Juego Centralizado**: Se eliminaron los `Thread`s separados para el movimiento de la pelota y la CPU. Toda la lógica del juego (física, IA y renderizado) ahora se ejecuta en un único **`javax.swing.Timer`**.
- **Seguridad en Swing (Thread-Safety)**: Al usar un `Timer`, todas las actualizaciones de estado y repintados ocurren en el **Event Dispatch Thread (EDT)** de Swing, lo que previene problemas de concurrencia y garantiza una animación estable y sin parpadeos.
- **Reducción de Carga**: Este cambio reduce significativamente la sobrecarga de la gestión de hilos, resultando en un menor consumo de CPU.

## Cómo Compilar y Ejecutar

Desde la raíz del proyecto, puedes compilar y ejecutar el juego con los siguientes comandos:

```bash
# Compilar todos los archivos .java y guardar los .class en el directorio 'bin'
javac -d bin src/*.java

# Ejecutar la clase principal desde el directorio 'bin'
java -cp bin pingPong
```

## Controles
- **Movimiento**: Usa las **teclas de flecha** o **W, A, S, D** para mover tu raqueta.

## Parámetros para Ajustar (en `pingPong.java`)
- `cpuAlpha`: Factor de suavizado de la IA (un valor más bajo la hace más reactiva).
- `cpuMaxSpeed`: Velocidad máxima de movimiento de la raqueta de la CPU.
- `largo`: Ancho de las raquetas.
- `diametro`: Tamaño de la pelota.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class pingPong extends JPanel implements ActionListener {

    // Pelota (posiciones en double para mejor física)
    private double x;
    private double y;
    private int diametro;
    private double dx; // velocidad horizontal
    private double dy; // velocidad vertical

    // Posicion que servira para la raqueta definbiendo su largo y ancho
    private int pos_x;
    private int pos_y;

    private int CPU_X;
    private int CPU_Y; // se fija cerca de la parte superior

    private final int ancho;
    private int largo;

    // variables para las fisicas
    // Control de juego
    //

    // variables que manejan el juego
    static pingPong juego;
    private ImageIcon imagenFondo;
    Font fuente;
    private int score;
    private int scoreCPU;
    private int nivel;
    private String s = "Puntuacion: ";
    private String s2 = "Nivel: ";
    private static int FRAME_DELAY_MS = 16; // ~60 FPS
    JFrame ventanaPerdida;
    static JFrame Ventana_Principal = new JFrame("Ping Pong");
    private Timer gameTimer;
    private boolean juegoActivo = true;
    private boolean derrotaMostrada = false;

    // IA (predicción + media móvil)
    private double cpuTargetXEMA = -1; // suavizado del objetivo X
    private double cpuAlpha = 0.25; // factor de suavizado
    private double cpuMaxSpeed = 5.0; // pixeles por frame

    // constructor inicial
    public pingPong() {
        this.x = num_Aleatorio();
        this.y = 100; // arranque algo bajo
        this.pos_x = 170;
        this.pos_y = 535; // paddle jugador abajo
        this.ancho = 15;
        this.largo = 80; // un poco más ancho
        this.diametro = 18;
        this.nivel = 1;
        this.score = 0;
        this.scoreCPU = 0;
        this.fuente = new Font("Monospaced", Font.BOLD, 15);
        this.imagenFondo = new ImageIcon("Assets/fondo2.gif");
        this.setSize(400, 600);
        this.CPU_Y = 30; // paddle CPU arriba fijo
        this.CPU_X = (400 / 2) - 40;

        // velocidad inicial (dirección aleatoria)
        double speed = 4.0;
        double angle = Math.toRadians(new Random().nextInt(120) + 30); // 30..150 grados
        this.dx = speed * Math.cos(angle);
        this.dy = Math.abs(speed * Math.sin(angle)); // hacia abajo al inicio
    }

    // metodo que genera los distintos sonidos
    public static void audio(int eleccion) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        switch (eleccion) {
            case 1: {
                AudioInputStream audio = AudioSystem
                        .getAudioInputStream((new File("Assets/musicaFondo.wav")).getAbsoluteFile());
                Clip sonido = AudioSystem.getClip();
                sonido.open(audio);
                sonido.start();
                sonido.loop(-1);
                break;
            }
            case 2: {
                AudioInputStream colision = AudioSystem
                        .getAudioInputStream((new File("Assets/colision.wav")).getAbsoluteFile());
                Clip sonido = AudioSystem.getClip();
                sonido.open(colision);
                sonido.start();
                break;
            }
            case 3: {
                // Usar sonido existente para evento de derrota
                AudioInputStream perdiste = AudioSystem
                        .getAudioInputStream((new File("Assets/colision.wav")).getAbsoluteFile());
                Clip sonido = AudioSystem.getClip();
                sonido.open(perdiste);
                sonido.start();
                break;
            }
            default:
                break;
        }

    }

    // genera un numero aleatorio para la posicion de la pelota
    public int num_Aleatorio() {
        Random r = new Random();
        return r.nextInt(360) + 20;
    }

    // usamos graphics sobre cargado
    @Override
    public void paint(Graphics g) {

        super.paint(g);
        g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.white);
        g.setFont(fuente);
        g.drawString(s2 + nivel, 0, 10);
        g.drawString("P1  " + s + score, 210, 10);
        g.drawString("CPU " + s + scoreCPU, 210, 30);

        g.setColor(Color.BLUE);
        g.fillRoundRect(CPU_X, CPU_Y, largo, ancho, ancho, ancho);
        g.setColor(Color.red);
        g.fillRoundRect(pos_x, pos_y, largo, ancho, ancho, ancho);
        g.setColor(Color.white);
        g.fillOval((int) Math.round(x), (int) Math.round(y), diametro, diametro);

    }

    // genera una ventana nueva que muestra que perdiste
    public void mensajeDerrota() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

    if(derrotaMostrada) return;
    derrotaMostrada = true;
    if(score >= scoreCPU){
            ventanaPerdida = new JFrame("Perdiste");
        }else{
            ventanaPerdida = new JFrame("Ganaste");
        }
        
        ventanaPerdida.setSize(300, 150);
        ventanaPerdida.setLocationRelativeTo(this);
        ventanaPerdida.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear el contenedor principal con BorderLayout
        ventanaPerdida.setLayout(new BorderLayout());

        // Agregar la imagen (gif) en la parte superior
        JLabel imagen = new JLabel(new ImageIcon("Assets/gameover.gif"));
        JLabel mensaje = new JLabel("¿Volver a jugar?");
        imagen.setHorizontalAlignment(SwingConstants.CENTER); // Centrar la imagen horizontalmente
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        ventanaPerdida.add(mensaje, "North");
        ventanaPerdida.add(imagen, BorderLayout.CENTER);

        // Crear un panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());

        // Crear los botones
        JButton continuar = new JButton("Si");
        JButton cancelar = new JButton("No");
        continuar.addActionListener(this);
        cancelar.addActionListener(this);
        panelBotones.add(continuar);
        panelBotones.add(cancelar);

        // Agregar el panel de botones a la parte inferior
        ventanaPerdida.add(panelBotones, BorderLayout.SOUTH);
        ventanaPerdida.setVisible(true);
    // Pausar animación
    if (gameTimer != null) gameTimer.stop();
    audioSeguro(3);
        System.out.println("Perdiste");

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Aquí defines las acciones de los botones
        String command = e.getActionCommand();
        // System.out.println(command);
        if (command.equals("Si")) {
            // Reiniciar el juego
            resetGame();
            ventanaPerdida.setVisible(false);
            if (gameTimer != null) gameTimer.start();
        } else {
            System.exit(ABORT);
        }
    }

    // metodo que mueve la raqueta
    public void posicionRaqueta(String op) {
        // System.out.println("Tama " + CPU_X + " X " + CPU_Y);
        if (op.equals("Left") && pos_x >= 0) {
            if (pos_x - 15 >= 0)
                pos_x = pos_x - 15;
        } else if (op.equals("Right") && pos_x <= 320) {
            if (pos_x + 15 <= 320)
                pos_x = pos_x + 15;
        } else if (op.equals("Up") && pos_y >= 0) {
            if (pos_y - 15 >= 0)
                pos_y = pos_y - 15;
        } else if (op.equals("Down") && (pos_y + ancho) <= this.getSize().height) {
            if ((pos_y + ancho + 15) < this.getSize().height)
                pos_y = pos_y + 15;
        }
    }

    private void moverCPU() {
        // Predice donde interceptará la pelota en la línea del CPU
        double targetX = predecirIntercepcionX(CPU_Y + ancho);
        if (cpuTargetXEMA < 0) cpuTargetXEMA = targetX;
        // Suavizado (media móvil exponencial) para un movimiento más humano
        cpuTargetXEMA = cpuAlpha * targetX + (1 - cpuAlpha) * cpuTargetXEMA;

        // Mover horizontalmente hacia el objetivo con límite de velocidad
        double centroCPU = CPU_X + (largo / 2.0);
        double delta = cpuTargetXEMA - centroCPU;
        double step = Math.max(-cpuMaxSpeed, Math.min(cpuMaxSpeed, delta));
        CPU_X += (int) Math.round(step);

        // Limites
        if (CPU_X < 0) CPU_X = 0;
        if (CPU_X + largo > getWidth()) CPU_X = Math.max(0, getWidth() - largo);
    }

    private double predecirIntercepcionX(int lineaY) {
        // Simula el movimiento de la pelota reflejando en paredes hasta alcanzar lineaY
        double simX = x + diametro / 2.0;
        double simY = y + diametro / 2.0;
        double vx = dx;
        double vy = dy;
        int maxIters = 2000; // seguridad
        int w = Math.max(1, getWidth());
    // int h = Math.max(1, getHeight()); // no se usa aquí
        for (int i = 0; i < maxIters; i++) {
            if ((vy < 0 && simY <= lineaY) || (vy > 0 && simY >= lineaY)) break;
            simX += vx;
            simY += vy;
            // rebotes laterales
            if (simX - diametro / 2.0 <= 0) { simX = diametro / 2.0; vx = Math.abs(vx); }
            if (simX + diametro / 2.0 >= w) { simX = w - diametro / 2.0; vx = -Math.abs(vx); }
        }
        return simX; // centro esperado
    }

    // metodo que verifica si pego en la raqueta o no
    private boolean colisionaCon(Rectangle paddle, int tolerancia) {
        // Rectángulo de la pelota (con pequeño margen)
        Rectangle ball = new Rectangle((int) Math.round(x), (int) Math.round(y), diametro, diametro);
        Rectangle paddleInflada = new Rectangle(paddle);
        paddleInflada.grow(tolerancia, tolerancia);
        return paddleInflada.intersects(ball);
    }

    // metodo que mueve la pelota
    private void actualizarFisica() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Evitar cálculos antes de tener tamaño válido
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        // mover pelota
        x += dx;
        y += dy;

    int w = getWidth();
    int h = getHeight();

        // Colisiones con paredes laterales
        if (x <= 0) {
            x = 0;
            dx = Math.abs(dx);
            audioSeguro(2);
        } else if (x + diametro >= w) {
            x = w - diametro;
            dx = -Math.abs(dx);
            audioSeguro(2);
        }

        // Paddles
        Rectangle paddleJugador = new Rectangle(pos_x, pos_y, largo, ancho);
        Rectangle paddleCPU = new Rectangle(CPU_X, CPU_Y, largo, ancho);

        // Jugador (abajo) - colisión superior de la raqueta
        if (dy > 0 && colisionaCon(paddleJugador, 2)) {
            // Recolocar justo arriba
            y = pos_y - diametro - 1;
            // Ajustar ángulo según punto de impacto
            double centroPelota = x + diametro / 2.0;
            double centroPaddle = pos_x + largo / 2.0;
            double offset = (centroPelota - centroPaddle) / (largo / 2.0); // -1..1
            double speed = Math.hypot(dx, dy) * 1.03; // leve aceleración
            double maxAngle = Math.toRadians(60);
            double angle = -maxAngle * offset; // hacia arriba
            dx = speed * Math.sin(angle);
            dy = -Math.abs(speed * Math.cos(angle));
            score += 10;
            if (score % 40 == 0) subirDificultad();
            audioSeguro(2);
        }

        // CPU (arriba) - colisión inferior de la raqueta
        if (dy < 0 && colisionaCon(paddleCPU, 2)) {
            y = CPU_Y + ancho + 1;
            double centroPelota = x + diametro / 2.0;
            double centroPaddle = CPU_X + largo / 2.0;
            double offset = (centroPelota - centroPaddle) / (largo / 2.0);
            double speed = Math.hypot(dx, dy) * (1.01 + (nivel * 0.01));
            double maxAngle = Math.toRadians(55);
            double angle = maxAngle * offset; // hacia abajo
            dx = speed * Math.sin(angle);
            dy = Math.abs(speed * Math.cos(angle));
            scoreCPU += 10;
            if (scoreCPU % 40 == 0) subirDificultad();
            audioSeguro(2);
        }

        // Pérdida del jugador
        if (y + diametro >= h) {
            if (!derrotaMostrada) {
                juegoActivo = false;
                mensajeDerrota();
            }
        }
    }

    private void subirDificultad() {
        nivel++;
        cpuMaxSpeed = Math.min(cpuMaxSpeed + 0.5, 10.0);
    }

    private void audioSeguro(int tipo) {
        try { audio(tipo); } catch (Exception ignored) {}
    }

    public static void movimientoCPU() {
        // no usado con el nuevo bucle, mantenido por compatibilidad
    }

     public static void mostrarVentanaInicio() {
        // Crear ventana de inicio
        JDialog ventanaInicio = new JDialog();
        ventanaInicio.setTitle("Instrucciones");
        ventanaInicio.setSize(300, 200);
        ventanaInicio.setLocationRelativeTo(null);
        ventanaInicio.setModal(true);

        // Configurar contenido
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("<html><center>El Jugador 1 comienza.<br>¡Presiona Start para jugar!</center></html>", JLabel.CENTER);
        JButton botonStart = new JButton("Start");

        // Agregar evento al botón
        botonStart.addActionListener(e -> ventanaInicio.dispose());

        // Añadir componentes al panel
        panel.add(label, BorderLayout.CENTER);
        panel.add(botonStart, BorderLayout.SOUTH);

        // Configurar la ventana
        ventanaInicio.add(panel);
        ventanaInicio.setVisible(true);
    }

    public static void main(String[] args)
            throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {

        mostrarVentanaInicio();
        // Crear ventana principal
        Ventana_Principal.setSize(400, 630);
        Ventana_Principal.setVisible(true);
        Ventana_Principal.setResizable(false);
        Ventana_Principal.setLocationRelativeTo(null);
        Ventana_Principal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ventana_Principal.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                String tecla = KeyEvent.getKeyText(e.getKeyCode());
                // System.out.println("La letra es " + tecla);

                if (tecla.equals("Left") || tecla.equals("A")) {
                    juego.posicionRaqueta("Left");
                } else if (tecla.equals("Right") || tecla.equals("D")) {
                    juego.posicionRaqueta("Right");
                } else if (tecla.equals("Up") || tecla.equals("W")) {
                    juego.posicionRaqueta("Up");
                } else if (tecla.equals("Down") || tecla.equals("S")) {
                    juego.posicionRaqueta("Down");
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });

        try {
            juego = new pingPong();
            Ventana_Principal.add(juego);
            audio(1);

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println(e.getMessage());
        }

        // Bucle de juego seguro para Swing (un solo hilo - EDT)
        juego.gameTimer = new Timer(FRAME_DELAY_MS, ev -> {
            try {
                if (juego.juegoActivo) {
                    juego.actualizarFisica();
                    juego.moverCPU();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            juego.repaint();
        });
        juego.gameTimer.start();

    }

    private void resetGame() {
        x = num_Aleatorio();
        y = 100;
        pos_x = 170;
        pos_y = 535;
        CPU_Y = 30;
        CPU_X = (getWidth() > 0 ? getWidth() : 400) / 2 - (largo / 2);
        score = 0;
        scoreCPU = 0;
        nivel = 1;
        cpuMaxSpeed = 5.0;
        cpuTargetXEMA = -1;
        double speed = 4.0;
        double angle = Math.toRadians(new Random().nextInt(120) + 30);
        dx = speed * Math.cos(angle);
        dy = Math.abs(speed * Math.sin(angle));
        juegoActivo = true;
    derrotaMostrada = false;
    }

}

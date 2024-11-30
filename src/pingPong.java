import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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

public class pingPong extends JPanel implements ActionListener {

    // Posicion que servira para la pelota y su diametro
    private static int x;
    private static int y;
    private static int diametro;

    // Posicion que servira para la raqueta definbiendo su largo y ancho
    private int pos_x;
    private int pos_y;

    private static int CPU_X;
    private static int CPU_Y;

    private final int ancho;
    private static int largo;

    // variables para las fisicas
    private boolean pelotaHaciaDerecha = true;
    private static boolean pelotaHaciaAbajo = true;

    // variables que manejan el juego
    static pingPong juego;
    private ImageIcon imagenFondo;
    Font fuente;
    private int score;
    private int scoreCPU;
    private int nivel;
    private String s = "Puntuacion: ";
    private String s2 = "Nivel: ";
    private static long velocidad = 10;
    private boolean reinicio = false;
    JFrame ventanaPerdida;
    static JFrame Ventana_Principal = new JFrame("Ping Pong");
    private Thread hiloMovimiento;
    private Thread hiloCPU;
    private static boolean turnoCPU = false;
    private static boolean juegoActivo = true;

    // constructor inicial
    public pingPong() {
        this.x = num_Aleatorio();
        this.pos_x = 170;
        this.pos_y = 535;
        this.ancho = 15;
        this.largo = 60;
        this.diametro = 20;
        this.nivel = 1;
        this.score = 0;
        this.scoreCPU = 0;
        this.fuente = new Font("Monospaced", Font.BOLD, 15);
        this.imagenFondo = new ImageIcon("Assets/fondo2.gif");
        this.setSize(400, 600);
        this.CPU_X = (400 / 2) - 30;
        this.CPU_Y = (600 / 2);
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
                AudioInputStream perdiste = AudioSystem
                        .getAudioInputStream((new File("Assets/muerte.wav")).getAbsoluteFile());
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
        g.fillOval(x, y, diametro, diametro);

    }

    // genera una ventana nueva que muestra que perdiste
    public void mensajeDerrota() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        if(!turnoCPU){
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
        velocidad = 80;
        audio(3);
        System.out.println("Perdiste");

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Aquí defines las acciones de los botones
        String command = e.getActionCommand();
        // System.out.println(command);
        if (command.equals("Si")) {
            // Reiniciar el juego
            reinicio = true;
            x = num_Aleatorio();
            y = 0;
            score = 0;
            scoreCPU = 0;
            nivel = 1;
            velocidad = 10;
            pelotaHaciaDerecha = true;
            pelotaHaciaAbajo = true;
            ventanaPerdida.setVisible(false);
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

    public static void posicionRaquetaCPU() {
        int[][] movimientos = new int[4][2];
        int[][] mejorMovimiento = new int[1][2];
        // voy a definir la primea fila para izquierda, segunda arriba, tercera derecha
        // y por ultimo abajo

        // defino un nuevo vector con el mejor movimiento y su valor, si es 0 es el
        // padre;
        while (true) {
            mejorMovimiento[0][0] = 0;
            mejorMovimiento[0][1] = distanciaEuclidiana(CPU_X, CPU_Y);
            if (turnoCPU && juegoActivo && pelotaHaciaAbajo) {
                for (int i = 1; i <= 4; i++) {
                    switch (i) {
                        case 1:
                            movimientos[0][0] = CPU_X - 15;
                            movimientos[0][1] = CPU_Y;
                        case 2:
                            movimientos[1][1] = CPU_Y - 15;
                            movimientos[1][0] = CPU_X;
                        case 3:
                            movimientos[2][0] = CPU_X + 15;
                            movimientos[2][1] = CPU_Y;
                        case 4:
                            movimientos[3][1] = CPU_Y + 15;
                            movimientos[3][0] = CPU_X;
                    }

                    // System.out.println("Padre " + mejorMovimiento[0][1] + " hijo "
                    // + distanciaEuclidiana(movimientos[i - 1][0], movimientos[i - 1][1]));
                    if (mejorMovimiento[0][1] > distanciaEuclidiana(movimientos[i - 1][0], movimientos[i - 1][1])) {
                        // System.out.println("Mejor movimiento " + i);
                        mejorMovimiento[0][0] = i;
                        mejorMovimiento[0][1] = distanciaEuclidiana(movimientos[i - 1][0], movimientos[i - 1][1]);
                    }
                }

                // System.out.println("Entre en "+ mejorMovimiento[0][0]);
                switch (mejorMovimiento[0][0]) {
                    case 1:
                        CPU_X = CPU_X - 15;
                        break;
                    case 2:
                        CPU_Y = CPU_Y - 15;
                        break;
                    case 3:
                        CPU_X = CPU_X + 15;
                        break;
                    case 4:
                        CPU_Y = CPU_Y + 15;
                        break;
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static int distanciaEuclidiana(int xr, int yr) {
        xr = (xr + largo) / 2;
        return (int) Math.sqrt((Math.pow((xr - ((x + diametro) / 2)), 2) + Math.pow((yr - (y + diametro)), 2)));
    }

    // metodo que verifica si pego en la raqueta o no
    public boolean pegoEnRaqueta(int rx) {
        if (x >= rx && x <= rx + largo) {
            turnoCPU = true;
            return true;
        }
        return false;
    }

    // metodo que mueve la pelota
    private void movimientoPelota() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        if (pelotaHaciaDerecha) {
            x = x + 1;
        } else {
            x = x - 1;
        }

        if (pelotaHaciaAbajo) {
            y = y + 1;
        } else {
            y = y - 1;
        }

        if (y == (pos_y - diametro) && !turnoCPU) {
            if (pegoEnRaqueta(pos_x)) {
                y = y - 1;
                score = score + 10;
                pelotaHaciaAbajo = false;
                pingPong.audio(2);
                if (nivel >= 1) {
                    if (scoreCPU >= nivel * (20)) {
                        velocidad = (long) (velocidad - nivel * (0.001));
                        nivel = nivel + 1;
                    }
                }
            }
        }

        if (y == (CPU_Y - diametro) && turnoCPU) {
            if (pegoEnRaqueta(CPU_X)) {
                CPU_Y = CPU_Y - 1;
                scoreCPU = scoreCPU + 10;
                pelotaHaciaAbajo = false;
                turnoCPU = false;
                pingPong.audio(2);
            }
        }

        if (y == diametro) {
            y = y + 1;
            pelotaHaciaAbajo = true;
            pingPong.audio(2);
        }

        if (y + diametro == getHeight()) {
            mensajeDerrota();
        }

        if (x == getWidth() - diametro) {
            x = x - 1;
            pelotaHaciaDerecha = false;
            pingPong.audio(2);
        }

        if (x == 0) {
            x = x + 1;
            pelotaHaciaDerecha = true;
            pingPong.audio(2);
        }
    }

    public static void movimientoCPU() {
        posicionRaquetaCPU();
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
                String tecla = e.getKeyText(e.getKeyCode());
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

        juego.hiloMovimiento = new Thread(() -> {
            while (true) {
                try {
                    juego.movimientoPelota();
                    Thread.sleep(velocidad);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        juego.hiloCPU = new Thread(() -> {
            while (true) {
                try {
                    movimientoCPU();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        juego.hiloMovimiento.start();
        juego.hiloCPU.start();

        while (true) {
            try {
                juego.repaint();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

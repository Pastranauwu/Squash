
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class score extends JPanel {

    Font fuente = new Font("Monospaced", Font.BOLD, 20);
    private int score;
    public score (){
        this.score = 0;
        this.setBackground(Color.blue);
        this.setSize(400, 100);
    }

    @Override
    public void paint(Graphics g) { 
        String s= "El score es \n Puntuacion " + score;
        super.paint(g);
        g.setFont(fuente);
        g.drawString(s, 100, 10);
    }

}

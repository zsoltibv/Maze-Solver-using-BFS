import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static void initUI() throws FileNotFoundException {

        //Dim Ecran de pe Calc
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int screen_width = (int) size.getWidth();
        int screen_height = (int) size.getHeight();

        JFrame f = new JFrame("Maze using BFS");
        f.setSize(screen_width, screen_height);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //add buttons
        JButton button = new JButton("Rezolva");
        button.setBounds(260, 10, 130, 30);
        f.add(button);

        //add buttons
        JButton button2 = new JButton("Reset");
        button2.setBounds(395, 10, 130, 30);
        f.add(button2);

        //add label + input
        Label lblStartNode = new Label("Nod start");
        lblStartNode.setBounds(30, 10, 60, 30);
        TextField startNode = new TextField();
        startNode.setBounds(100, 10, 150, 30);
        f.add(lblStartNode);
        f.add(startNode);

        //add maze panel
        View mazePanel = new View();
        f.add(mazePanel);

        ArrayList<Integer> mazeExits = View.mazeExits;

        mazePanel.borderMatrix();
        mazePanel.makeAdjMatrix();
        AtomicInteger i = new AtomicInteger(0);

        button.addActionListener(e -> {
            mazePanel.clearGrid();
            int starting_point = Integer.parseInt(startNode.getText()) - 1;
            if (i.get() < mazeExits.size()) {
                if(starting_point == mazeExits.get(i.get()) -1){
                    i.getAndIncrement();
                }
                mazePanel.getShortestDistance(starting_point, mazeExits.get(i.get()) - 1);
                i.getAndIncrement();
            } else {
                System.out.println("Nu mai exista drumuri.");
            }
        });

        button2.addActionListener(e -> {
            try {
                mazeExits.clear();
                f.dispose();
                initUI();
            } catch (FileNotFoundException e2) {
                throw new RuntimeException(e2);
            }
        });

        f.setLayout(null);
        f.setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException {

        SwingUtilities.invokeLater(new Runnable() //new Thread()
        {
            public void run() {
                try {
                    initUI();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
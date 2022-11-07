import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class View extends JPanel {
    public static ArrayList<Block> grid = null;
    private int RECT_SIZE = 100;

    public static int nr_coloane;
    static ArrayList<ArrayList<Integer>> mazeMatrix;    // 1 is road, 0 is wall
    static ArrayList<Integer> mazeExits = new ArrayList<>();
    int[][] borderedMatrix;
    int[][] adjMatrix;
    int[] pred; //matricea de predecesori
    int[] dist; //matricea distanta
    static int nr_col = 0;
    static int nr_linii = 0;
    static int nr_noduri = 0;
    private int dr[] = {-1, 1, 0, 0};
    private int dc[] = {0, 0, 1, -1};

    public View() throws FileNotFoundException {
        ReadInput();
        setBounds(30, 60, RECT_SIZE * nr_col, RECT_SIZE * nr_linii);
        // Build the grid
        grid = new ArrayList<Block>(nr_noduri);
        for (int y = 0; y < nr_linii; y++) {
            for (int x = 0; x < nr_col; x++) {
                Color color;
                if (mazeMatrix.get(y).get(x) == 0) {
                    color = Color.BLACK;
                } else {
                    if (y == 0 || x == 0 || y == nr_linii - 1 || x == nr_col - 1) {
                        mazeExits.add(y * nr_col + x + 1);
                    }
                    color = Color.WHITE;
                }
                Block rect = new Block(x * RECT_SIZE, y * RECT_SIZE, RECT_SIZE, y * nr_col + x + 1, color);
                grid.add(rect);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // paint the grid
        for (Block r : grid) {

            g.setColor(r.getColor());
//            g.drawRect(r.getX(), r.getY(), r.getSize(), r.getSize());
            g.fillRect(r.getX(), r.getY(), r.getSize() - 1, r.getSize() - 1);
            g.setColor(Color.BLUE);
            if (r.getNumber() < 10) {
                g.drawString(((Integer) r.getNumber()).toString(), r.getX() + 8, r.getY() + 15);
            }else{
                g.drawString(((Integer) r.getNumber()).toString(), r.getX() + 4, r.getY() + 15);
            }
        }
    }

    public void clearGrid(){
        for (Block r : grid) {
            if(r.getColor() == Color.RED) {
                r.setColor(Color.WHITE);
            }
        }
    }

    public void ReadInput() throws FileNotFoundException {
        mazeMatrix = new ArrayList<ArrayList<Integer>>();
        Scanner input = new Scanner(new File("fisier-labirint.txt"));
        while (input.hasNextLine()) {
            Scanner colReader = new Scanner(input.nextLine());
            ArrayList<Integer> col = new ArrayList<>();
            while (colReader.hasNextInt()) {
                col.add(colReader.nextInt());
            }
            mazeMatrix.add(col);
        }
        nr_linii = mazeMatrix.size();
        nr_col = mazeMatrix.get(0).size();
        nr_noduri = nr_linii * nr_col;
        System.out.println(nr_linii + " " + nr_col + " " + nr_noduri);
    }

    public void borderMatrix() {
        borderedMatrix = new int[nr_linii + 2][nr_col + 2];
        //bordare
        for (int i = 0; i < nr_linii + 2; i++) {
            for (int j = 0; j < nr_col + 2; j++) {
                borderedMatrix[i][j] = -1;
            }
        }
        //copiere
        for (int i = 1; i <= nr_linii; i++) {
            for (int j = 1; j <= nr_col; j++) {
                borderedMatrix[i][j] = mazeMatrix.get(i - 1).get(j - 1);
            }
        }
    }

    public void printBorderedMatrix() {
        for (int i = 0; i < nr_linii + 2; i++) {
            for (int j = 0; j < nr_col + 2; j++) {
                System.out.print(borderedMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void makeAdjMatrix() {
        adjMatrix = new int[nr_linii * nr_col][nr_linii * nr_col];
        for (int i = 0; i < nr_linii * nr_col; i++) {
            for (int j = 0; j < nr_linii * nr_col; j++) {
                adjMatrix[i][j] = 0;
            }
        }

        for (int i = 1; i <= nr_linii; i++) {
            for (int j = 1; j <= nr_col; j++) {
                if (borderedMatrix[i][j] == 1) {
                    for (int k = 0; k < 4; k++) {
                        int ci = i + dr[k], cj = j + dc[k];
                        if (borderedMatrix[ci][cj] == 1) {
//                            System.out.println((i - 1) * nr_linii + j - 1);
//                            System.out.println((ci - 1) * nr_linii + cj - 1);
                            adjMatrix[(i - 1) * nr_col + j - 1][(ci - 1) * nr_col + cj - 1] = 1;
                        }
                    }
                }
            }
        }
    }

    public void printAdjMatrix() {
        //print
        for (int i = 0; i < nr_linii * nr_col; i++) {
            for (int j = 0; j < nr_linii * nr_col; j++) {
                System.out.print(adjMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void adjToList() {
        for (int i = 0; i < nr_linii * nr_col; i++) {
            for (int j = 0; j < nr_linii * nr_col; j++) {
                if (adjMatrix[i][j] == 1) {
                    System.out.println((i + 1) + "->" + (j + 1));
                }
            }
        }
    }

    void getShortestDistance(int start, int end) {
        pred = new int[nr_noduri];
        dist = new int[nr_noduri];

        if (!bfs(start, end)) {
            System.out.println("Intrarea si iesirea nu sunt conectate.");
            return;
        }

        java.util.List<Integer> path = new ArrayList<>();
        int crawl = end;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }

//         Print distance
        System.out.println("Lungimea drumului este: " + dist[end]);

        System.out.print("Drumul este: ");
        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.print(path.get(i) + " ");
            grid.get(path.get(i)).setColor(Color.RED);
            repaint();
        }
        System.out.println();
    }

    boolean bfs(int start, int end) {
        boolean[] visited = new boolean[nr_noduri];

        List<Integer> q = new ArrayList<>();

        for (int i = 0; i < nr_noduri; i++) {
            visited[i] = false;
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }

        // Set source as visited
        visited[start] = true;
        dist[start] = 0;
        q.add(start);

        int vis;
        while (!q.isEmpty()) {
            vis = q.get(0);

            // Print the current node
            q.remove(q.get(0));

            for (int i = 0; i < nr_linii * nr_col; i++) {
                if (adjMatrix[vis][i] == 1 && (!visited[i])) {

                    visited[i] = true;
                    dist[i] = dist[vis] + 1;
                    pred[i] = vis;

                    q.add(i);
                    // Set

                    if (i == end) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

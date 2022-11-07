import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Block {
    private int x;
    private int y;
    private int size;
    private int number;
    private Color color;

    public Block(int x, int y, int width, int number, Color color) {
        this.x = x;
        this.y = y;
        this.size = width;
        this.number = number;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

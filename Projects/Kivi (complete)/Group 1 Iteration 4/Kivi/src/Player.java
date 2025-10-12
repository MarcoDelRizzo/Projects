
import java.awt.Color;
import java.io.Serializable;

public abstract class Player implements Serializable {
    // Fields for shared attributes
    private static final long serialVersionUID = 1L;
    protected String name;
    protected Color color;
    protected int score;
    protected int stones;
    protected boolean isTurn;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
        this.stones = 10;
        this.isTurn = false;
    }

    // Getters and setters for shared fields
    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getStones() {
        return stones;
    }

    public boolean getIsTurn() {
        return isTurn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(int r, int g, int b) {
        if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
            this.color = new Color(r, g, b);
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStones(int stones) {
        this.stones = stones;
    }

    public void setIsTurn(boolean t) {
        this.isTurn = t;
    }

    // Common game logic
    public void switchTurn() {
        this.isTurn = !isTurn;
    }

    public void decrementStones() {
        this.stones--;
    }

    public void addScore(int scoreChange) {
        this.score = score + scoreChange;
    }

    public void decrementScore(int scoreChange) {
        this.score = score - scoreChange;
    }
}

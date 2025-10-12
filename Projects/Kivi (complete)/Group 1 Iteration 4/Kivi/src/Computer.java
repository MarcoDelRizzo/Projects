import java.awt.Color;
import java.io.Serializable;

public class Computer extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Difficulty { EASY, HARD }
    private Difficulty difficulty;

    public Computer(String name, Color color, Difficulty difficulty) {
        super(name, color);
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}

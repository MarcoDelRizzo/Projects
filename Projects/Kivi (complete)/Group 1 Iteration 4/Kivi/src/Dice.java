import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public class Dice implements Serializable {
    private static final long serialVersionUID = 1L;
    private int diceValue;
    private String diceImagePath;
    // Mark random transient since its state is not crucial to persist.
    transient private Random random;

    public Dice() {
        this.diceValue = 1;
        this.diceImagePath = "images/dice1.png";
        this.random = new Random();
    }

    public void roll() {
        this.diceValue = random.nextInt(6) + 1;
        this.diceImagePath = "images/dice" + this.diceValue + ".png";
    }

    public int getDiceValue() {
        return diceValue;
    }

    public String getDiceImage() {
        return diceImagePath;
    }
    
    public void reset() {
        this.diceValue = 1;
        this.diceImagePath = "images/dice1.png";
    }
    
    // Custom deserialization to reinitialize transient fields.
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        random = new Random();
    }
}

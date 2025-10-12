
import java.util.Random;


// score and combinations class needs to be implemented 
public class Dice {
    private int diceValue; 
    private String diceImagePath; 
    private Random random;

    public Dice() {
        this.diceValue = 1; //default value
        this.diceImagePath = "images/dice1.png"; //default image
        this.random = new Random();
    }

    public void roll() {
        this.diceValue = random.nextInt(6) + 1;
        // Ensure path includes "images/" directory
        this.diceImagePath = "images/dice" + this.diceValue + ".png";
    }

    public int getDiceValue() {
        return diceValue;
    }

    public String getDiceImage() {
        return diceImagePath;
    }
    
    public void reset()
    {
    	 this.diceValue = 1;
    	 this.diceImagePath = "images/dice1.png";
    }
}
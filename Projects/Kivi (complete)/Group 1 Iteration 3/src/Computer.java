import java.awt.Color;
import java.io.Serializable;

/**
 * Class representing a Computer in the game.
 * Difficulty is left intentially ambiguous for sake of discussed implementation.
 * @author ncarter
 */
public class Computer extends Player implements Serializable {
    //Fields of Computer
    private static final long serialVersionUID = 1L;
    private String name;
    private Color color;
    private int score;
    private int stones;
    private boolean isTurn;
    //private ****** difficulty;     Leaving this ambiguous for now, not sure how it will be implemented

    public Computer(String name, Color color)
    {
    	super(name, color); // Call the constructor of GameParticipant
    
        //this.difficulty = *****;
    }
}

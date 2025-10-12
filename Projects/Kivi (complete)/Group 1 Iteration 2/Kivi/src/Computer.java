import java.awt.Color;

/**
 * Class representing a Computer in the game.
 * Difficulty is left intentially ambiguous for sake of discussed implementation.
 * @author ncarter
 */
public class Computer extends Player {
    //Fields of Computer
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

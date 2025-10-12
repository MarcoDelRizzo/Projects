import java.awt.Color;

/**
 * Class representing a player of the game.
 * @author ncarter
 */
public class Human extends Player {
    //Fields of Player
    private String name;
    private Color color;
    private int score;
    private int stones;
    private boolean isTurn;

    public Human(String name, Color color)
    {
    	super(name, color); 
    }
}

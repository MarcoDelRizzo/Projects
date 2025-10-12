import java.awt.Color;
import java.io.Serializable;

/**
 * Class representing a player of the game.
 * @author ncarter
 */
public class Human extends Player implements Serializable {
    //Fields of Player
    private static final long serialVersionUID = 1L;
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
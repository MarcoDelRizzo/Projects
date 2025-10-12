import java.awt.*;
import java.io.Serializable;

class Square implements Serializable{
    private static final long serialVersionUID = 1L;
    Color backgroundColor;
    Color stoneColor;
    transient Image image;
    Color originalColor;

    public Square(Color backgroundColor, Image image) {
        this.backgroundColor = backgroundColor;
        this.originalColor = backgroundColor;
        this.image = image;
        this.stoneColor = null; //no stoneColor initially
    }
}
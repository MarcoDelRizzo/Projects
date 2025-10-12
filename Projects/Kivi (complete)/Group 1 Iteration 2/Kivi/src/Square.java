import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


class Square{
    Color backgroundColor;
    Color stoneColor;
    Image image;
    Color originalColor;

    public Square(Color backgroundColor, Image image) {
        this.backgroundColor = backgroundColor;
        this.originalColor = backgroundColor;
        this.image = image;
        this.stoneColor = null; //no stoneColor initially
    }
}
import java.io.Serializable;
//maybe for later development

public class Stone implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String colour;
    private String texture;
    private String stoneImagePath;
    
    public Stone(String colour, String texture){
        
        this.colour = colour;
        this.texture = texture;
        updateImagePath();
    }
    
    public void updateImagePath(){
        this.stoneImagePath = "images/stone_" + colour + "_" + texture + ".png";
    }
    
    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
        updateImagePath();
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
        updateImagePath();
    }

    public String getStoneImage() {
        return stoneImagePath;
    }
}
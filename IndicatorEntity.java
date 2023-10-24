import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import engine.ChessPiece;

public class IndicatorEntity extends Entity {
    public static Image img;
    public IndicatorEntity() {
        super();
        if(IndicatorEntity.img == null) {
            String imgPath = System.getProperty("user.dir") + "/textures/indicator.png";
            File imgFile = new File(imgPath);
            img = new ImageIcon(imgFile.getAbsolutePath()).getImage();
        }
        updateTextureImage(img);
    }
}

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import engine.ChessPiece;

public class FloorTileEntity extends Entity {
    public static Image blackImg;
    public static Image whiteImg;
    public FloorTileEntity(byte floorColor) {
        super();
        Image newImg = ChessPiece.isColor(floorColor, ChessPiece.Black) ? blackImg : whiteImg;
        if(ChessPiece.isColor(floorColor, ChessPiece.Black) && FloorTileEntity.blackImg == null) {
            String imgPath = System.getProperty("user.dir") + "\\textures\\blackTile.png";
            File imgFile = new File(imgPath);
            blackImg = new ImageIcon(imgFile.getAbsolutePath()).getImage();
            newImg = blackImg;
        } else if(ChessPiece.isColor(floorColor, ChessPiece.White) && FloorTileEntity.whiteImg == null){
            String imgPath = System.getProperty("user.dir") + "\\textures\\whiteTile.png";
            File imgFile = new File(imgPath);
            whiteImg = new ImageIcon(imgFile.getAbsolutePath()).getImage();
            newImg = whiteImg;
        }
        updateTextureImage(newImg);
    }
}

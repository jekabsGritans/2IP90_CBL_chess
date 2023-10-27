import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import engine.ChessPiece;

public class FloorTileEntity extends Entity {
    public static Color blackTile = Color.decode("#596A37");
    public static Color whiteTile = Color.decode("#FFFFE3");

    public FloorTileEntity(byte floorColor) {
        super();
        graphic.setOpaque(true);
        setColor(ChessPiece.isColor(floorColor, ChessPiece.White) ? whiteTile : blackTile);
    }
}

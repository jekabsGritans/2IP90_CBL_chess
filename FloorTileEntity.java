import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import engine.ChessPiece;

public class FloorTileEntity extends Entity {
    public static Color blackTile = Color.decode("#596A37");
    public static Color whiteTile = Color.decode("#FFFFE3");

    /**
     * Constructor that calls entity constructor and also sets appropriate color
     * @param floorColor the color to which the file is set, depending on bits xxx??xxx
     */
    public FloorTileEntity(byte floorColor) {
        super();
        graphic.setOpaque(true);
        setColor(ChessPiece.isColor(floorColor, ChessPiece.White) ? whiteTile : blackTile);
    }
}
